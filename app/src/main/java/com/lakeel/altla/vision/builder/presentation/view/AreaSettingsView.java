package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.builder.presentation.model.SceneBuildModel;
import com.lakeel.altla.vision.domain.model.AreaScope;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface AreaSettingsView {

    void onUpdateRadioGroupChecked(int checkedId);

    void onUpdateAreaName(@Nullable String areaName);

    void onUpdateAreaDescriptionName(@Nullable String areaDescriptionName);

    void onUpdateAreaDescriptionPickerEnabled(boolean enabled);

    void onUpdateEditButtonEnabled(boolean enabled);

    void onShowAreaFindByPlaceView(@NonNull AreaScope areaScope);

    void onShowAreaFindByNameView(@NonNull AreaScope areaScope);

    void onShowUserAreaDescriptionListInAreaView(@NonNull String areaId);

    void onShowUserSceneEditView(@NonNull SceneBuildModel sceneBuildModel);

    void onSnackbar(@StringRes int resId);
}
