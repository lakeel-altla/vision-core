package com.lakeel.altla.vision.builder.presentation.model;

import com.google.firebase.database.Exclude;

import com.lakeel.altla.vision.ArgumentNullException;

import java.util.Date;

public final class UserAreaDescriptionModel {

    public final String areaDescriptionId;

    public String name;

    public Date creationDate;

    @Exclude
    public boolean fileCached;

    @Exclude
    public boolean fileUploaded;

    public UserAreaDescriptionModel(String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        this.areaDescriptionId = areaDescriptionId;
    }
}
