package com.lakeel.altla.vision.builder.presentation.view;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserActorEditView {

    void onUpdateViewsEnabled(boolean enabled);

    void onUpdateActionSave(boolean enabled);

    void onUpdateHomeAsUpIndicator(@DrawableRes int resId);

    void onUpdateHomeAsUpIndicator(@Nullable Drawable drawable);

    void onUpdateTitle(@Nullable String title);

    void onUpdateName(@Nullable String name);

    void onUpdatePositionX(double x);

    void onUpdatePositionY(double y);

    void onUpdatePositionZ(double z);

    void onUpdateOrientationX(double x);

    void onUpdateOrientationY(double y);

    void onUpdateOrientationZ(double z);

    void onUpdateOrientationW(double w);

    void onUpdateScaleX(double x);

    void onUpdateScaleY(double y);

    void onUpdateScaleZ(double z);

    void onShowNameError(@StringRes int resId);

    void onHideNameError();

    void onBackView();

    void onSnackbar(@StringRes int resId);
}
