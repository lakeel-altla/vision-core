package com.lakeel.altla.vision.domain.usecase;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.vision.data.repository.firebase.UserDeviceRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserProfileRepository;
import com.lakeel.altla.vision.domain.model.UserDevice;
import com.lakeel.altla.vision.domain.model.UserProfile;

import android.os.Build;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class SignInWithGoogleUseCase {

    @Inject
    UserProfileRepository userProfileRepository;

    @Inject
    UserDeviceRepository userDeviceRepository;

    @Inject
    public SignInWithGoogleUseCase() {
    }

    @NonNull
    public Completable execute(@NonNull GoogleSignInAccount googleSignInAccount) {
        return Single.just(googleSignInAccount)
                     .flatMap(this::signIn)
                     .flatMap(this::ensureUserProfile)
                     .flatMapCompletable(this::saveUserDevice)
                     .subscribeOn(Schedulers.io());
    }

    @NonNull
    private Single<FirebaseUser> signIn(@NonNull GoogleSignInAccount googleSignInAccount) {
        return Single.create(e -> {
            AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
            Task<AuthResult> task = FirebaseAuth.getInstance().signInWithCredential(credential);

            task.addOnSuccessListener(authResult -> e.onSuccess(authResult.getUser()));
            task.addOnFailureListener(e::onError);
        });
    }

    @NonNull
    private Single<FirebaseUser> ensureUserProfile(@NonNull FirebaseUser firebaseUser) {
        // Check if the user profile exists.
        return Single.create(e -> userProfileRepository.find(firebaseUser.getUid(), userProfile -> {
            // Create the user profile if it does not exist.
            if (userProfile == null) {
                userProfile = new UserProfile(firebaseUser.getUid());
                userProfile.displayName = firebaseUser.getDisplayName();
                userProfile.email = firebaseUser.getEmail();
                if (firebaseUser.getPhotoUrl() != null) {
                    userProfile.photoUri = firebaseUser.getPhotoUrl().toString();
                }

                userProfileRepository.save(userProfile);
            }
            e.onSuccess(firebaseUser);
        }, e::onError));
    }

    @NonNull
    private Completable saveUserDevice(@NonNull FirebaseUser firebaseUser) {
        return Completable.create(e -> {
            UserDevice userDevice = new UserDevice(firebaseUser.getUid(), FirebaseInstanceId.getInstance().getId());
            userDevice.createdAt = FirebaseInstanceId.getInstance().getCreationTime();
            userDevice.osName = "android";
            userDevice.osModel = Build.MODEL;
            userDevice.osVersion = Build.VERSION.RELEASE;

            userDeviceRepository.save(userDevice);

            e.onComplete();
        });
    }
}
