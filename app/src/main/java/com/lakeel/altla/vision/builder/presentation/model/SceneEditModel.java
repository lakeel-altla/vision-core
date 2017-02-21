package com.lakeel.altla.vision.builder.presentation.model;

import android.support.annotation.NonNull;

public final class SceneEditModel {

    public final String areaId;

    public final String areaDescriptionId;

    public final String sceneId;

    public SceneEditModel(@NonNull String areaId, @NonNull String areaDescriptionId, @NonNull String sceneId) {
        this.areaId = areaId;
        this.areaDescriptionId = areaDescriptionId;
        this.sceneId = sceneId;
    }
}
