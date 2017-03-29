package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.model.AreaScope;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

public interface AreaModeView {

    void onUpdateRadioGroupScopeChecked(@IdRes int checkedId);

    void onAreaModeSelected(@NonNull AreaScope areaScope);

    void onCloseView();
}
