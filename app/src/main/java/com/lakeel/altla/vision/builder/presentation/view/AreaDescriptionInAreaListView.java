package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface AreaDescriptionInAreaListView {

    void onItemInserted(int position);

    void onItemChanged(int position);

    void onItemRemoved(int position);

    void onItemMoved(int fromPosition, int toPosition);

    void onDataSetChanged();

    void onItemSelected(@NonNull String areaDescriptionId);

    void onSnackbar(@StringRes int resId);
}
