package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.model.Scope;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

public interface AreaModeView {

    void onUpdateRadioGroupScopeChecked(@IdRes int checkedId);

    void onAreaModeSelected(@NonNull Scope scope);

    void onCloseView();
}
