package com.lakeel.altla.vision.domain.model;

import com.lakeel.altla.vision.ArgumentNullException;

public final class UserTexture {

    public final String textureId;

    public final String name;

    public UserTexture(String textureId, String name) {
        if (textureId == null) throw new ArgumentNullException("textureId");
        if (name == null) throw new ArgumentNullException("name");

        this.textureId = textureId;
        this.name = name;
    }
}
