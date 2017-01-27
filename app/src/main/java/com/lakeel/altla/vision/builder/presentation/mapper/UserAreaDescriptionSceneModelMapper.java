package com.lakeel.altla.vision.builder.presentation.mapper;

import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionSceneModel;
import com.lakeel.altla.vision.domain.model.UserAreaDescriptionScene;

import android.support.annotation.NonNull;

public final class UserAreaDescriptionSceneModelMapper {

    private UserAreaDescriptionSceneModelMapper() {
    }

    @NonNull
    public static UserAreaDescriptionSceneModel map(@NonNull UserAreaDescriptionScene userAreaDescriptionScene) {
        UserAreaDescriptionSceneModel model = new UserAreaDescriptionSceneModel();
        model.sceneId = userAreaDescriptionScene.sceneId;
        return model;
    }
}
