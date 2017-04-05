package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface ActorView {

    void onUpdateName(@Nullable String name);

    void onUpdateCreatedAt(long createdAt);

    void onUpdateUpdatedAt(long updatedAt);

    void onUpdateMainMenuVisible(boolean visible);

    void onCloseView();

    void onSnackbar(@StringRes int resId);
}
