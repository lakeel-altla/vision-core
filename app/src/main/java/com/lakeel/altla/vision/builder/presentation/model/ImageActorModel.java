package com.lakeel.altla.vision.builder.presentation.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

public final class ImageActorModel extends ActorModel {

    public Bitmap bitmap;

    public ImageActorModel(@NonNull String userId, @NonNull String sceneId, @NonNull String actorId,
                           @NonNull String assetId) {
        super(userId, sceneId, actorId, assetId);
    }
}
