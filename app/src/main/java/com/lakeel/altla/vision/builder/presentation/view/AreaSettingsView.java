package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.domain.model.AreaScope;
import com.lakeel.altla.vision.domain.model.AreaSettings;

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

    void onShowAreaDescriptionInAreaListView(@NonNull AreaScope areaScope, @NonNull String areaId);

    void onShowArView(@NonNull AreaSettings settings);

    void onSnackbar(@StringRes int resId);
}
