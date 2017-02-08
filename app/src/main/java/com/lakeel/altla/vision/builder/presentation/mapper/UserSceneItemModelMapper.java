package com.lakeel.altla.vision.builder.presentation.mapper;

import com.lakeel.altla.vision.builder.presentation.model.UserSceneItemModel;
import com.lakeel.altla.vision.domain.model.UserScene;

import android.support.annotation.NonNull;

public final class UserSceneItemModelMapper {

    private UserSceneItemModelMapper() {
    }

    @NonNull
    public static UserSceneItemModel map(@NonNull UserScene userScene) {
        UserSceneItemModel model = new UserSceneItemModel();
        model.sceneId = userScene.sceneId;
        model.name = userScene.name;
        return model;
    }
}
