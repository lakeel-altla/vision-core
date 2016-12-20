package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.vision.domain.model.UserConnection;
import com.lakeel.altla.vision.domain.repository.ConnectionRepository;
import com.lakeel.altla.vision.domain.repository.UserConnectionRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class ObserveConnectionUseCase {

    @Inject
    ConnectionRepository connectionRepository;

    @Inject
    UserConnectionRepository userConnectionRepository;

    @Inject
    public ObserveConnectionUseCase() {
    }

    public Observable<UserConnection> execute() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        String userId = user.getUid();
        String instanceId = FirebaseInstanceId.getInstance().getId();
        UserConnection userConnection = new UserConnection(userId, instanceId);

        return connectionRepository.observe()
                                   .flatMap(connected -> registerUserConnection(userConnection, connected))
                                   .map(connected -> userConnection)
                                   .subscribeOn(Schedulers.io());
    }

    private Observable<Boolean> registerUserConnection(UserConnection userConnection, Boolean connected) {
        if (connected) {
            return userConnectionRepository.setOnline(userConnection)
                                           .toObservable()
                                           .map(_userConnection -> connected);
        } else {
            return Observable.just(connected);
        }
    }
}
