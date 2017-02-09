package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.StringRes;

public interface UserSceneCreateView {

    void onUpdateButtonCreateEnabled(boolean enabled);

    void onCreated();

    void onSnackbar(@StringRes int resId);
}
