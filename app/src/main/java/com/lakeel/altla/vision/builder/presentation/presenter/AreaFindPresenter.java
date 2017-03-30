package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.AreaFindView;
import com.lakeel.altla.vision.model.Scope;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public final class AreaFindPresenter extends BasePresenter<AreaFindView> {

    private static final String ARG_SCOPE_VALUE = "scopeValue";

    private Scope scope;

    @Inject
    public AreaFindPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull Scope scope) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SCOPE_VALUE, scope.getValue());
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        int scopeValue = arguments.getInt(ARG_SCOPE_VALUE, -1);
        if (scopeValue < 0) {
            throw new IllegalArgumentException(String.format("Argument '%s' is required.", ARG_SCOPE_VALUE));
        }

        scope = Scope.toAreaScope(scopeValue);
        if (scope == Scope.UNKNOWN) throw new IllegalArgumentException("Unknown scope.");
    }

    public void onClickButtonFindByPlace() {
        getView().onShowPlacePicker();
    }

    public void onPlacePicked(@NonNull Place place) {
        getView().onShowAreaByPlaceListView(scope, place);
    }

    public void onShowPlacePickerFailed(@NonNull Exception e) {
        getView().onSnackbar(R.string.snackbar_failed);
    }

    public void onClickButtonClose() {
        getView().onCloseView();
    }
}
