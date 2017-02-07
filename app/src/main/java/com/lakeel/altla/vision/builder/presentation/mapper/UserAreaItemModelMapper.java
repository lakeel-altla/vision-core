package com.lakeel.altla.vision.builder.presentation.mapper;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.builder.presentation.model.UserAreaItemModel;
import com.lakeel.altla.vision.domain.model.UserArea;

import android.support.annotation.NonNull;

public final class UserAreaItemModelMapper {

    private UserAreaItemModelMapper() {
    }

    @NonNull
    public static final UserAreaItemModel map(@NonNull UserArea userArea) {
        UserAreaItemModel model = new UserAreaItemModel();
        model.areaId = userArea.areaId;
        model.name = userArea.name;
        model.placeId = userArea.placeId;
        model.level = String.valueOf(userArea.level);
        return model;
    }

    @NonNull
    public static final UserAreaItemModel map(@NonNull UserAreaItemModel model, @NonNull Place place) {
        model.placeName = place.getName().toString();
        model.placeAddress = place.getAddress().toString();
        return model;
    }
}
