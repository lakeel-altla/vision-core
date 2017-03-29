package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.model.AreaScope;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface AreaSettingsView {

    void onUpdateAreaMode(@StringRes int resId);

    void onUpdateAreaName(@Nullable String areaName);

    void onUpdateAreaDescriptionName(@Nullable String areaDescriptionName);

    void onUpdateButtonSelectAreaDescriptionEnabled(boolean enabled);

    void onUpdateButtonStartEnabled(boolean enabled);

    void onShowAreaSettingsHistoryView();

    void onShowAreaModeView(@NonNull AreaScope areaScope);

    void onShowAreaFindView(@NonNull AreaScope areaScope);

    void onShowAreaDescriptionByAreaListView(@NonNull AreaScope areaScope, @NonNull Area area);

    void onUpdateArView(@NonNull String areaSettingsId);

    void onCloseView();
}
