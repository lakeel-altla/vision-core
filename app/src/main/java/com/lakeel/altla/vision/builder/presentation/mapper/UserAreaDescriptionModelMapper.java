package com.lakeel.altla.vision.builder.presentation.mapper;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionModel;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

import java.util.Date;

public final class UserAreaDescriptionModelMapper {

    private UserAreaDescriptionModelMapper() {
    }

    @NonNull
    public static UserAreaDescriptionModel map(@NonNull UserAreaDescription userAreaDescription) {
        UserAreaDescriptionModel model = new UserAreaDescriptionModel(userAreaDescription.areaDescriptionId);
        model.name = userAreaDescription.name;
        model.creationDate = new Date(userAreaDescription.creationTime);
        model.placeId = userAreaDescription.placeId;
        model.level = String.valueOf(userAreaDescription.level);
        return model;
    }

    @NonNull
    public static UserAreaDescriptionModel map(@NonNull UserAreaDescriptionModel model, @NonNull Place place) {
        model.placeName = place.getName().toString();
        model.placeAddress = place.getAddress().toString();
        return model;
    }
}
