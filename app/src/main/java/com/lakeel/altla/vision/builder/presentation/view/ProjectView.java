package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.builder.presentation.model.ProjectModel;

import android.support.annotation.NonNull;

public interface ProjectView {

    void onShowUserAreaListView();

    void onShowUserAreaDescriptionListInAreaView(@NonNull String areaId);

    void onShowUserSceneListInAreaView(@NonNull String areaId);

    void onShowUserSceneEditView(@NonNull String sceneId);

    void onModelUpdated(@NonNull ProjectModel model);

    void onUpdateViewsDependOnUserAreaEnabled(boolean enabled);

    void onUpdateEditButtonEnabled(boolean enabled);
}
