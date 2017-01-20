package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class DownloadUserAreaDescriptionFileUseCase {

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    UserAreaDescriptionFileRepository userAreaDescriptionFileRepository;

    @Inject
    public DownloadUserAreaDescriptionFileUseCase() {
    }

    public Completable execute(String areaDescriptionId, OnProgressListener onProgressListener) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return Completable.create(e -> {
            File file = areaDescriptionCacheRepository.getFile(areaDescriptionId);
            userAreaDescriptionFileRepository.download(
                    user.getUid(), areaDescriptionId, file,
                    aVoid -> e.onComplete(),
                    e::onError,
                    onProgressListener);
        }).subscribeOn(Schedulers.io());
    }
}
