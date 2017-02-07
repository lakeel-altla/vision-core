package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.builder.presentation.model.TextureItemModel;

import android.support.annotation.NonNull;

public interface TextureItemView {

    void onModelUpdated(@NonNull TextureItemModel model);

    void onShowProgress(int max, int progress);

    void onHideProgress();

    void onStartDrag();

    void onSelect(int selectedPosition, boolean selected);

    void onShowDeleteTextureConfirmationDialog();
}
