package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.builder.presentation.model.SceneBuildModel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface ProjectView {

    void onUpdateAreaName(@Nullable String areaName);

    void onUpdateAreaDescriptionName(@Nullable String areaDescriptionName);

    void onUpdateSceneName(@Nullable String sceneName);

    void onUpdateAreaDescriptionPickerEnabled(boolean enabled);

    void onUpdateScenePickerEnabled(boolean enabled);

    void onUpdateEditButtonEnabled(boolean enabled);

    void onShowUserAreaListView();

    void onShowUserAreaDescriptionListInAreaView(@NonNull String areaId);

    void onShowUserSceneListInAreaView(@NonNull String areaId);

    void onShowUserSceneEditView(@NonNull SceneBuildModel sceneBuildModel);
}
