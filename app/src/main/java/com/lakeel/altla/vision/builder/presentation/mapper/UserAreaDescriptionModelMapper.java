package com.lakeel.altla.vision.builder.presentation.mapper;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionItemModel;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

import java.util.Date;

public final class UserAreaDescriptionModelMapper {

    private UserAreaDescriptionModelMapper() {
    }

    @NonNull
    public static UserAreaDescriptionItemModel map(@NonNull UserAreaDescription userAreaDescription) {
        UserAreaDescriptionItemModel model = new UserAreaDescriptionItemModel(userAreaDescription.areaDescriptionId);
        model.name = userAreaDescription.name;
        model.creationDate = new Date(userAreaDescription.createdAt);
        return model;
    }

    @NonNull
    public static UserAreaDescriptionItemModel map(@NonNull UserAreaDescriptionItemModel model, @NonNull Place place) {
        return model;
    }
}
