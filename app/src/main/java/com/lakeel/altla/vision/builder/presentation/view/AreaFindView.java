package com.lakeel.altla.vision.builder.presentation.view;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.model.AreaScope;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface AreaFindView {

    void onShowPlacePicker();

    void onShowAreaByPlaceListView(@NonNull AreaScope areaScope, @NonNull Place place);

    void onCloseView();

    void onSnackbar(@StringRes int resId);
}
