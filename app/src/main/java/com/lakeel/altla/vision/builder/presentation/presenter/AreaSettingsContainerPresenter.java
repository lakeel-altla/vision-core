package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.builder.presentation.model.AreaSettingsModel;
import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsContainerView;
import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.model.AreaDescription;
import com.lakeel.altla.vision.model.AreaScope;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public final class AreaSettingsContainerPresenter extends BasePresenter<AreaSettingsContainerView> {

    private static final String STATE_MODEL = "model";

    private AreaSettingsModel model;

    private boolean initialDisplay;

    @Inject
    public AreaSettingsContainerPresenter() {
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (savedInstanceState == null) {
            model = new AreaSettingsModel();
            model.setAreaScope(AreaScope.USER);

            initialDisplay = true;
        } else {
            model = Parcels.unwrap(savedInstanceState.getParcelable(STATE_MODEL));
            if (model == null) {
                throw new IllegalStateException(String.format("State '%s' is required.", STATE_MODEL));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_MODEL, Parcels.wrap(model));
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        if (initialDisplay) {
            getView().onShowAreaSettingsView(model);
        }
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();
    }

    @Override
    protected void onStopOverride() {
        super.onStopOverride();
    }

    public void onAreaModeSelected(@NonNull AreaScope areaScope) {
        model.setAreaScope(areaScope);
    }

    public void onPlaceSelected(@NonNull Place place) {
        getView().onShowAreaByPlaceListView(model.getAreaScope(), place);
    }

    public void onAreaSelected(@NonNull Area area) {
        model.setArea(area);
    }

    public void onAreaDescriptionSelected(@NonNull AreaDescription areaDescription) {
        model.setAreaDescription(areaDescription);
    }
}
