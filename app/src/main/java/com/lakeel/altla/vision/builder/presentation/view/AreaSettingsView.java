package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.builder.presentation.model.AreaSettingsModel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface AreaSettingsView {

    void onUpdateAreaMode(@StringRes int resId);

    void onUpdateAreaName(@Nullable String areaName);

    void onUpdateAreaDescriptionName(@Nullable String areaDescriptionName);

    void onUpdateButtonSelectAreaDescriptionEnabled(boolean enabled);

    void onShowAreaModeView(@NonNull AreaSettingsModel model);

    void onShowAreaFindView(@NonNull AreaSettingsModel model);

    void onShowAreaDescriptionByAreaListView(@NonNull AreaSettingsModel model);

    void onAreaSettingsSelected(@NonNull AreaSettingsModel model);

    void onCloseView();
}
