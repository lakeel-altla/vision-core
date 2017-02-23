package com.lakeel.altla.vision.builder.presentation.model;

import android.support.annotation.NonNull;

public final class SceneBuildModel {

    public final String areaId;

    public final String areaDescriptionId;

    public final String sceneId;

    public SceneBuildModel(@NonNull String areaId, @NonNull String areaDescriptionId, @NonNull String sceneId) {
        this.areaId = areaId;
        this.areaDescriptionId = areaDescriptionId;
        this.sceneId = sceneId;
    }
}
