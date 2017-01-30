package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface UserAreaDescriptionsView {

    void updateItems();

    void updateItem(int position);

    void showSnackbar(@StringRes int resId);

    void showUserAreaDescriptionScenes(@NonNull String areaDescriptionId);
}
