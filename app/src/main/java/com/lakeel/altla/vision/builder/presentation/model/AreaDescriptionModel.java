package com.lakeel.altla.vision.builder.presentation.model;

import com.google.firebase.database.Exclude;

import com.lakeel.altla.vision.ArgumentNullException;

import java.util.Date;

public final class AreaDescriptionModel {

    public final String areaDescriptionId;

    public String name;

    public Date creationDate;

    public String placeId;

    public String placeName;

    public String placeAddress;

    public String level;

    @Exclude
    public boolean fileCached;

    @Exclude
    public boolean fileUploaded;

    public AreaDescriptionModel(String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        this.areaDescriptionId = areaDescriptionId;
    }
}
