package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.AreaFindView;
import com.lakeel.altla.vision.model.AreaScope;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public final class AreaFindPresenter extends BasePresenter<AreaFindView> {

    private static final String ARG_AREA_SCOPE_VALUE = "areaScopeValue";

    private AreaScope areaScope;

    @Inject
    public AreaFindPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull AreaScope areaScope) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_AREA_SCOPE_VALUE, areaScope.getValue());
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        int areaScopeValue = arguments.getInt(ARG_AREA_SCOPE_VALUE, -1);
        if (areaScopeValue < 0) {
            throw new IllegalArgumentException(String.format("Argument '%s' is required.", ARG_AREA_SCOPE_VALUE));
        }

        areaScope = AreaScope.toAreaScope(areaScopeValue);

        if (areaScope == AreaScope.UNKNOWN) {
            throw new IllegalArgumentException(String.format("Argument '%s' is invalid.", ARG_AREA_SCOPE_VALUE));
        }
    }

    public void onClickButtonFindByPlace() {
        getView().onShowPlacePicker();
    }

    public void onPlacePicked(@NonNull Place place) {
        getView().onShowAreaByPlaceListView(areaScope, place);
    }

    public void onShowPlacePickerFailed(@NonNull Exception e) {
        getView().onSnackbar(R.string.snackbar_failed);
    }

    public void onClickButtonClose() {
        getView().onCloseView();
    }
}
