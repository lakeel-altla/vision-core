package com.lakeel.altla.vision.builder.presentation.view;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.builder.presentation.model.AreaSettingsModel;
import com.lakeel.altla.vision.model.AreaScope;

import android.support.annotation.NonNull;

public interface AreaSettingsContainerView {

    void onShowAreaSettingsView(@NonNull AreaSettingsModel model);

    void onShowAreaByPlaceListView(@NonNull AreaScope areaScope, @NonNull Place place);
}
