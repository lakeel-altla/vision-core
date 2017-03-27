package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.model.Area;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface AreaByPlaceListView {

    void onUpdateButtonSelectEnabled(boolean enabled);

    void onItemInserted(int position);

    void onItemChanged(int position);

    void onItemRemoved(int position);

    void onItemMoved(int fromPosition, int toPosition);

    void onDataSetChanged();

    void onAreaSelected(@NonNull Area area);

    void onBackToAreaFindView();

    void onCloseAreaByPlaceListView();

    void onSnackbar(@StringRes int resId);
}
