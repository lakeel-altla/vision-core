package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.StringRes;

public interface AreaDescriptionListView {

    void updateItems();

    void updateItem(int position);

    void loadAreaDescription(String areaDescriptionId);

    void showSnackbar(@StringRes int resId);
}
