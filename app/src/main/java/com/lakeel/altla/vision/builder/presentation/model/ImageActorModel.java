package com.lakeel.altla.vision.builder.presentation.model;

import com.lakeel.altla.vision.model.Actor;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

public final class ImageActorModel extends ActorModel {

    public Bitmap bitmap;

    public ImageActorModel(@NonNull Actor actor) {
        super(actor);
    }
}
