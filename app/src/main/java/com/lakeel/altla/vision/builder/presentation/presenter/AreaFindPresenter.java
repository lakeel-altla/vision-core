package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.AreaFindView;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.support.annotation.NonNull;

import javax.inject.Inject;

public final class AreaFindPresenter extends BasePresenter<AreaFindView> {

    @Inject
    public AreaFindPresenter() {
    }

    public void onClickButtonFindByPlace() {
        getView().onShowPlacePicker();
    }

    public void onPlacePicked(@NonNull Place place) {
        getView().onPlaceSelected(place);
    }

    public void onShowPlacePickerFailed(@NonNull Exception e) {
        getView().onSnackbar(R.string.snackbar_failed);
    }

    public void onClickButtonClose() {
        getView().onCloseAreaFindView();
    }
}
