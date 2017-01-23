package com.lakeel.altla.vision.builder.presentation.model;

import com.lakeel.altla.vision.ArgumentNullException;

import java.util.Date;

public final class AreaDescriptionModel {

    public final String areaDescriptionId;

    public final String name;

    public final Date creationDate;

    public boolean current;

    public AreaDescriptionModel(String areaDescriptionId, String name, long creationTime) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");
        if (name == null) throw new ArgumentNullException("name");

        this.areaDescriptionId = areaDescriptionId;
        this.name = name;
        this.creationDate = new Date(creationTime);
    }
}
