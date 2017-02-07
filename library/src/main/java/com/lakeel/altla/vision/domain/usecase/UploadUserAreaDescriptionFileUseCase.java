package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public final class UploadUserAreaDescriptionFileUseCase {

    @Inject
    UserAreaDescriptionFileRepository userAreaDescriptionFileRepository;

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    private final Consumer<? super InputStream> closeStream = stream -> {
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    @Inject
    public UploadUserAreaDescriptionFileUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull String areaDescriptionId, OnProgressListener onProgressListener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        // Convert arguments to the internal model.
        return Single.just(new Model(user.getUid(), areaDescriptionId, onProgressListener))
                     // Open the stream of the area description file as cache.
                     .flatMap(this::createCacheStream)
                     // Upload it to Firebase Storage.
                     .flatMap(this::uploadUserAreaDescriptionFile)
                     // Mark the user area description in Firebase database as uploaded.
                     .flatMapCompletable(this::markAsUploaded)
                     .subscribeOn(Schedulers.io());
    }

    private Single<Model> createCacheStream(Model model) {
        return Single.create(e -> {
            File file = areaDescriptionCacheRepository.getFile(model.areaDescriptionId);
            model.stream = new FileInputStream(file);
            // Get the total bytes of it.
            model.totalBytes = model.stream.available();
            e.onSuccess(model);
        });
    }

    private Single<Model> uploadUserAreaDescriptionFile(
            Model model) {
        return Completable
                .using(() -> model.stream,
                       stream -> Completable.create(e -> userAreaDescriptionFileRepository.upload(
                               model.userId, model.areaDescriptionId, model.stream,
                               aVoid -> e.onComplete(),
                               e::onError,
                               (totalBytes, bytesTransferred) ->
                                       model.onProgressListener.onProgress(model.totalBytes, bytesTransferred))),
                       closeStream)
                .toSingleDefault(model);
    }

    private Completable markAsUploaded(Model model) {
        return Completable.create(e -> {
            userAreaDescriptionRepository.find(model.userId, model.areaDescriptionId, userAreaDescription -> {
                if (userAreaDescription == null) {
                    throw new IllegalStateException(
                            String.format("UserAreaDescription not found: userId = %s, areaDescriptionId = %s",
                                          model.userId, model.areaDescriptionId));
                }
                userAreaDescription.fileUploaded = true;
                userAreaDescriptionRepository.save(userAreaDescription);
                e.onComplete();
            }, e::onError);
        });
    }

    private final class Model {

        final String userId;

        final String areaDescriptionId;

        final OnProgressListener onProgressListener;

        InputStream stream;

        long totalBytes;

        Model(String userId, String areaDescriptionId, OnProgressListener onProgressListener) {
            this.userId = userId;
            this.areaDescriptionId = areaDescriptionId;
            this.onProgressListener = onProgressListener;
        }
    }
}
