package com.lakeel.altla.vision.builder.presentation.model;

import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;

import android.support.annotation.NonNull;

public class ActorModel {

    public final String userId;

    public final String sceneId;

    public final String actorId;

    public final String assetId;

    public final Vector3 position = new Vector3();

    public final Quaternion orientation = new Quaternion();

    public final Vector3 scale = new Vector3();

    public long createdAt = -1;

    public long updatedAt = -1;

    protected ActorModel(@NonNull String userId, @NonNull String sceneId, @NonNull String actorId,
                         @NonNull String assetId) {
        this.userId = userId;
        this.sceneId = sceneId;
        this.actorId = actorId;
        this.assetId = assetId;
    }
}
