package com.lakeel.altla.vision.builder.presentation.view;

import com.google.android.gms.location.places.Place;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface AreaFindView {

    void onShowPlacePicker();

    void onPlaceSelected(@NonNull Place place);

    void onCloseAreaFindView();

    void onSnackbar(@StringRes int resId);
}
