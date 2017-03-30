package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.api.CurrentUser;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsView;
import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.model.AreaDescription;
import com.lakeel.altla.vision.model.AreaSettings;
import com.lakeel.altla.vision.model.Scope;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public final class AreaSettingsPresenter extends BasePresenter<AreaSettingsView> {

    private static final String ARG_SCOPE = "scope";

    @Inject
    VisionService visionService;

    private Scope initialScope;

    private AreaSettings areaSettings;

    private Scope scope;

    private Area area;

    private AreaDescription areaDescription;

    @Inject
    public AreaSettingsPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull Scope scope) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_SCOPE, Parcels.wrap(scope));
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        initialScope = Parcels.unwrap(arguments.getParcelable(ARG_SCOPE));
        if (initialScope == null) {
            throw new ArgumentNullException(String.format("Argument '%s' is required.", ARG_SCOPE));
        }

        scope = initialScope;

        // TODO: restore saved fields.
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        int resId = (scope == Scope.PUBLIC ? R.string.label_area_mode_public : R.string.label_area_mode_user);
        getView().onUpdateAreaMode(resId);
        getView().onUpdateAreaName(area == null ? null : area.getName());
        getView().onUpdateAreaDescriptionName(areaDescription == null ? null : areaDescription.getName());

        getView().onUpdateButtonSelectAreaDescriptionEnabled(canSelectAreaDescription());
        getView().onUpdateButtonStartEnabled(canStart());
    }

    public void onAreaModeSelected(@NonNull Scope scope) {
        if (this.scope != scope) {
            this.scope = scope;
            area = null;
            areaDescription = null;

            int resId =
                    (scope == Scope.PUBLIC ? R.string.label_area_mode_public : R.string.label_area_mode_user);
            getView().onUpdateAreaMode(resId);
            getView().onUpdateAreaName(null);
            getView().onUpdateAreaDescriptionName(null);
            getView().onUpdateButtonSelectAreaDescriptionEnabled(canSelectAreaDescription());
            getView().onUpdateButtonStartEnabled(canStart());
        }
    }

    public void onAreaSelected(@Nullable Area area) {
        String oldAreaId = (this.area == null) ? null : this.area.getId();
        String newAreaId = (area == null) ? null : area.getId();

        if (oldAreaId == null || !oldAreaId.equals(newAreaId)) {
            this.area = area;
            areaDescription = null;

            getView().onUpdateAreaName(area == null ? null : area.getName());
            getView().onUpdateAreaDescriptionName(null);
            getView().onUpdateButtonSelectAreaDescriptionEnabled(canSelectAreaDescription());
            getView().onUpdateButtonStartEnabled(canStart());
        }
    }

    public void onAreaDescriptionSelected(@Nullable AreaDescription areaDescription) {
        this.areaDescription = areaDescription;

        getView().onUpdateAreaDescriptionName(areaDescription == null ? null : areaDescription.getName());
        getView().onUpdateButtonSelectAreaDescriptionEnabled(canSelectAreaDescription());
        getView().onUpdateButtonStartEnabled(canStart());
    }

    public void onAreaSettingsSelected(@NonNull AreaSettings areaSettings,
                                       @NonNull Area area,
                                       @NonNull AreaDescription areaDescription) {
        this.areaSettings = areaSettings;

        onAreaModeSelected(areaSettings.getAreaScopeAsEnum());
        onAreaSelected(area);
        onAreaDescriptionSelected(areaDescription);
    }

    public void onClickButtonClose() {
        getView().onCloseView();
    }

    public void onClickButtonHistory() {
        getView().onShowAreaSettingsHistoryView();
    }

    public void onClickButtonSelectAreaMode() {
        getView().onShowAreaModeView(scope);
    }

    public void onClickButtonSelectArea() {
        getView().onShowAreaFindView(scope);
    }

    public void onClickButtonSelectAreaDescription() {
        getView().onShowAreaDescriptionByAreaListView(scope, area);
    }

    public void onClickButtonStart() {
        if (areaSettings == null) {
            areaSettings = new AreaSettings();
            areaSettings.setUserId(CurrentUser.getInstance().getUserId());
        }

        areaSettings.setAreaScopeAsEnum(scope);
        areaSettings.setAreaId(area.getId());
        areaSettings.setAreaDescriptionId(areaDescription.getId());

        visionService.getUserAreaSettingsApi()
                     .saveUserAreaSettings(areaSettings);

        getView().onUpdateArView(areaSettings.getId());
        getView().onCloseView();
    }

    private boolean canSelectAreaDescription() {
        return area != null;
    }

    private boolean canStart() {
        return area != null && areaDescription != null;
    }
}
