package com.lakeel.altla.vision.data.repository.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.lakeel.altla.rx.firebase.storage.RxFirebaseStorageTask;
import com.lakeel.altla.rx.tasks.RxGmsTask;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;

import java.io.File;
import java.io.InputStream;

import rx.Completable;
import rx.Single;

public final class UserTextureFileRepository extends BaseStorageRepository {

    private static final String PATH_USER_TEXTURES = "userTextures";

    public UserTextureFileRepository(FirebaseStorage storage) {
        super(storage);
    }

    public Completable save(String userId, String textureId, InputStream stream,
                            OnProgressListener onProgressListener) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (textureId == null) throw new ArgumentNullException("textureId");
        if (stream == null) throw new ArgumentNullException("stream");

        return Single.<StorageReference>create(subscriber -> {
            StorageReference reference = getRootReference().child(PATH_USER_TEXTURES)
                                                           .child(userId)
                                                           .child(textureId);
            subscriber.onSuccess(reference);
        }).flatMapCompletable(reference -> {
            UploadTask task = reference.putStream(stream);
            return RxFirebaseStorageTask.asCompletable(task, onProgressListener::onProgress);
        });
    }

    public Completable delete(String userId, String textureId) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (textureId == null) throw new ArgumentNullException("textureId");

        return Single.<StorageReference>create(subscriber -> {
            StorageReference reference = getRootReference().child(PATH_USER_TEXTURES)
                                                           .child(userId)
                                                           .child(textureId);
            subscriber.onSuccess(reference);
        }).flatMapCompletable(reference -> {
            Task<Void> task = reference.delete();
            return RxGmsTask.asCompletable(task);
        });
    }

    public Completable download(String userId, String textureId, File destination,
                                OnProgressListener onProgressListener) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (textureId == null) throw new ArgumentNullException("textureId");
        if (destination == null) throw new ArgumentNullException("destination");

        return Single.<StorageReference>create(subscriber -> {
            StorageReference reference = getRootReference().child(PATH_USER_TEXTURES)
                                                           .child(userId)
                                                           .child(textureId);
            subscriber.onSuccess(reference);
        }).flatMapCompletable(reference -> {
            FileDownloadTask task = reference.getFile(destination);
            return RxFirebaseStorageTask.asCompletable(task, onProgressListener::onProgress);
        });
    }
}
