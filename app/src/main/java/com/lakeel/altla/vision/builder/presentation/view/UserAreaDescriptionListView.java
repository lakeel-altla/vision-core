package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.StringRes;

public interface UserAreaDescriptionListView {

    void updateItems();

    void updateItem(int position);

    void showSnackbar(@StringRes int resId);
}
