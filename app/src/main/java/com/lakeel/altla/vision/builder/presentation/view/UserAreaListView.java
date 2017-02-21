package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.StringRes;

public interface UserAreaListView {

    void onItemInserted(int position);

    void onItemChanged(int position);

    void onItemRemoved(int position);

    void onItemMoved(int fromPosition, int toPosition);

    void onDataSetChanged();

    void onItemSelected(String areaId);

    void onSnackbar(@StringRes int resId);
}
