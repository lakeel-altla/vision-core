package com.lakeel.altla.vision.builder.presentation.model;

import android.support.annotation.NonNull;

public final class AreaDescriptionModel {

    public final String areaDescriptionId;

    public final String name;

    public boolean synced;

    public AreaDescriptionModel(@NonNull String areaDescriptionId, @NonNull String name) {
        this.areaDescriptionId = areaDescriptionId;
        this.name = name;
    }
}
