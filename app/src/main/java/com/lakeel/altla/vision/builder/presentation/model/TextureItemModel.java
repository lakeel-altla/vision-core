package com.lakeel.altla.vision.builder.presentation.model;

import android.graphics.Bitmap;

public final class TextureItemModel {

    public final String textureId;

    public final String name;

    public Bitmap bitmap;

    public TextureItemModel(String textureId, String name) {
        this.textureId = textureId;
        this.name = name;
    }
}
