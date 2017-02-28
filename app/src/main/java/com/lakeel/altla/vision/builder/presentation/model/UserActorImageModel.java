package com.lakeel.altla.vision.builder.presentation.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

public final class UserActorImageModel extends UserActorModel {

    public Bitmap bitmap;

    public UserActorImageModel(@NonNull String userId, @NonNull String sceneId, @NonNull String actorId,
                               @NonNull String assetId) {
        super(userId, sceneId, actorId, assetId);
    }
}
