package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserTexture;
import com.lakeel.altla.vision.domain.repository.UserTextureRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindUserTextureUseCase {

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    public FindUserTextureUseCase() {
    }

    public Observable<UserTexture> execute(String textureId) {
        if (textureId == null) throw new ArgumentNullException("textureId");

        return userTextureRepository.find(textureId)
                                    .subscribeOn(Schedulers.io());
    }
}
