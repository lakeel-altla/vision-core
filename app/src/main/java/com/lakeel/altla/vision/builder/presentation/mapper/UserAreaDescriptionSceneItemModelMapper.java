package com.lakeel.altla.vision.builder.presentation.mapper;

import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionSceneItemModel;
import com.lakeel.altla.vision.domain.model.UserAreaDescriptionScene;

import android.support.annotation.NonNull;

public final class UserAreaDescriptionSceneItemModelMapper {

    private UserAreaDescriptionSceneItemModelMapper() {
    }

    @NonNull
    public static UserAreaDescriptionSceneItemModel map(@NonNull UserAreaDescriptionScene userAreaDescriptionScene) {
        UserAreaDescriptionSceneItemModel model = new UserAreaDescriptionSceneItemModel();
        model.sceneId = userAreaDescriptionScene.sceneId;
        return model;
    }
}
