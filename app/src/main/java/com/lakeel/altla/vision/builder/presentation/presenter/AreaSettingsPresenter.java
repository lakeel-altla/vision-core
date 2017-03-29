package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.api.CurrentUser;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsView;
import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.model.AreaDescription;
import com.lakeel.altla.vision.model.AreaScope;
import com.lakeel.altla.vision.model.AreaSettings;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public final class AreaSettingsPresenter extends BasePresenter<AreaSettingsView> {

    private static final String ARG_AREA_SCOPE_VALUE = "areaScopeValue";

    @Inject
    VisionService visionService;

    private AreaScope initialAreaScope;

    private AreaSettings areaSettings;

    private AreaScope areaScope;

    private Area area;

    private AreaDescription areaDescription;

    @Inject
    public AreaSettingsPresenter() {
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

        initialAreaScope = AreaScope.toAreaScope(areaScopeValue);

        if (initialAreaScope == AreaScope.UNKNOWN) {
            throw new IllegalArgumentException(
                    String.format("Argument '%s' is invalid: value = %s", ARG_AREA_SCOPE_VALUE, areaScope));
        }

        areaScope = initialAreaScope;

        // TODO: restore saved fields.
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        int resId = (areaScope == AreaScope.PUBLIC ? R.string.label_area_mode_public : R.string.label_area_mode_user);
        getView().onUpdateAreaMode(resId);
        getView().onUpdateAreaName(area == null ? null : area.getName());
        getView().onUpdateAreaDescriptionName(areaDescription == null ? null : areaDescription.getName());

        getView().onUpdateButtonSelectAreaDescriptionEnabled(canSelectAreaDescription());
        getView().onUpdateButtonStartEnabled(canStart());
    }

    public void onAreaModeSelected(@NonNull AreaScope areaScope) {
        if (this.areaScope != areaScope) {
            this.areaScope = areaScope;
            area = null;
            areaDescription = null;

            int resId =
                    (areaScope == AreaScope.PUBLIC ? R.string.label_area_mode_public : R.string.label_area_mode_user);
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
        getView().onShowAreaModeView(areaScope);
    }

    public void onClickButtonSelectArea() {
        getView().onShowAreaFindView(areaScope);
    }

    public void onClickButtonSelectAreaDescription() {
        getView().onShowAreaDescriptionByAreaListView(areaScope, area);
    }

    public void onClickButtonStart() {
        if (areaSettings == null) {
            areaSettings = new AreaSettings();
            areaSettings.setUserId(CurrentUser.getInstance().getUserId());
        }

        areaSettings.setAreaScopeAsEnum(areaScope);
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
