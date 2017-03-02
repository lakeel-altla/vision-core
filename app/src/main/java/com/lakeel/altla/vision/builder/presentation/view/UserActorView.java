package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public interface UserActorView {

    void onUpdateTitle(@Nullable String title);

    void onUpdateActorId(@NonNull String actorId);

    void onUpdatePosition(double x, double y, double z);

    void onUpdateOrientation(double x, double y, double z, double w);

    void onUpdateScale(double x, double y, double z);

    void onUpdateCreatedAt(long createdAt);

    void onUpdateUpdatedAt(long updatedAt);

    void onShowUserActorEditView(@NonNull String sceneId, @NonNull String actorId);

    void onSnackbar(@StringRes int resId);
}
