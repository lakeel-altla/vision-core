package com.lakeel.altla.vision.builder.presentation.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Defines the sign in view.
 */
public interface SignInView {

    void onCloseSignInView();

    void onStartActivityForResult(@NonNull Intent intent, int requestCode);

    void onSnackbar(@StringRes int resId);

    void onShowProgressDialog();

    void onHideProgressDialog();
}
