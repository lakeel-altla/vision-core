package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserProfile;
import com.lakeel.altla.vision.domain.repository.UserProfileRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class ObserveUserProfileUseCase {

    @Inject
    UserProfileRepository userProfileRepository;

    @Inject
    public ObserveUserProfileUseCase() {
    }

    public Observable<UserProfile> execute(String userId) {
        if (userId == null) throw new ArgumentNullException("userId");

        return userProfileRepository.observe(userId)
                                    .subscribeOn(Schedulers.io());
    }
}
