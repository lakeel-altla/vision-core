package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.model.Scope;

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

    void onShowAreaModeView(@NonNull Scope scope);

    void onShowAreaFindView(@NonNull Scope scope);

    void onShowAreaDescriptionByAreaListView(@NonNull Scope scope, @NonNull Area area);

    void onUpdateArView(@NonNull String areaSettingsId);

    void onCloseView();
}
