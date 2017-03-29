package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.StringRes;

public interface ImageAssetListView {

    void onUpdateImageButtonExpandVisible(boolean visible);

    void onUpdateImageButtonCollapseVisible(boolean visible);

    void onUpdateContentVisible(boolean visible);

    void onItemInserted(int position);

    void onItemChanged(int position);

    void onItemRemoved(int position);

    void onItemMoved(int fromPosition, int toPosition);

    void onDataSetChanged();

    void onSnackbar(@StringRes int resId);
}
