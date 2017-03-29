package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface AreaSettingsItemView {

    void onUpdateUpdatedAt(long updatedAt);

    void onUpdateAreaMode(@StringRes int resId);

    void onUpdateAreaName(@Nullable String areaName);

    void onUpdateAreaDescriptionName(@Nullable String areaDescriptionName);
}
