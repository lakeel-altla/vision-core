package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public final class FindAllUserAreaDescriptionsUseCase {

    private static final Log LOG = LogFactory.getLog(FindAllUserAreaDescriptionsUseCase.class);

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    public FindAllUserAreaDescriptionsUseCase() {
    }

    public Observable<UserAreaDescription> execute() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        String userId = user.getUid();

        return Observable.<UserAreaDescription>create(e -> {
            userAreaDescriptionRepository.findAll(userId, userAreaDescriptions -> {
                for (UserAreaDescription userAreaDescription : userAreaDescriptions) {
                    userAreaDescription.fileCached = areaDescriptionCacheRepository.exists(
                            userAreaDescription.areaDescriptionId);

                    e.onNext(userAreaDescription);
                }

                e.onComplete();
            }, e::onError);
        }).subscribeOn(Schedulers.io());
    }
}
