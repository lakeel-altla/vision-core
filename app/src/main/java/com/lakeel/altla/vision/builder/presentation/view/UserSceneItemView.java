package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.builder.presentation.model.UserSceneItemModel;

import android.support.annotation.NonNull;

public interface UserSceneItemView {

    void onModelUpdated(@NonNull UserSceneItemModel model);
}
