package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.model.Scope;

import android.support.annotation.NonNull;

public interface AreaSettingsView {

    void onShowAreaSettingsHistoryView();

    void onShowAreaModeView(@NonNull Scope scope);

    void onShowAreaFindView(@NonNull Scope scope);

    void onShowAreaDescriptionByAreaListView(@NonNull Scope scope, @NonNull Area area);

    void onUpdateArView(@NonNull String areaSettingsId);

    void onCloseView();
}
