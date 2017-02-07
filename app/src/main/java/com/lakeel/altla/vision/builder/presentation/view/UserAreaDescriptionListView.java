package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface UserAreaDescriptionListView {

    void onItemsUpdated();

    void onItemInserted(int position);

    void onSnackbar(@StringRes int resId);

    void onShowUserAreaDescriptionScenesView(@NonNull String areaDescriptionId);
}
