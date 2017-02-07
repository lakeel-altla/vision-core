package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.builder.presentation.model.EditTextureModel;

import android.support.annotation.StringRes;

public interface RegisterTextureView {

    void onShowLocalTexturePicker();

    void onUpdateTextureVisible(boolean visible);

    void onUpdateLoadTextureProgressVisible(boolean visible);

    void onModelUpdated(EditTextureModel model);

    void onShowUploadProgressDialog();

    void onUpdateUploadProgressDialogProgress(long max, long diff);

    void onHideUploadProgressDialog();

    void onSnackbar(@StringRes int resId);
}
