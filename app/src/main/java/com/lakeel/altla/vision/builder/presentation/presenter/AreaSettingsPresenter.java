package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.vision.api.CurrentUser;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsView;
import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.model.AreaDescription;
import com.lakeel.altla.vision.model.AreaScope;
import com.lakeel.altla.vision.model.AreaSettings;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcel;
import org.parceler.Parcels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class AreaSettingsPresenter extends BasePresenter<AreaSettingsView> {

    private static final String STATE_MODEL = "model";

    @Inject
    VisionService visionService;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Model model;

    @Inject
    public AreaSettingsPresenter() {
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (savedInstanceState != null) {
            model = Parcels.unwrap(savedInstanceState.getParcelable(STATE_MODEL));
            if (model == null) {
                throw new IllegalStateException(String.format("Instance state '%s' must be not null.", STATE_MODEL));
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

        getView().onUpdateAreaDescriptionPickerEnabled(false);
        getView().onUpdateEditButtonEnabled(false);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        if (model == null) {
            Disposable disposable = Maybe
                    .<AreaSettings>create(e -> {
                        visionService.getUserAreaSettingsApi().findUserCurrentAreaSettings(areaSettings -> {
                            if (areaSettings == null) {
                                e.onComplete();
                            } else {
                                e.onSuccess(areaSettings);
                            }
                        }, e::onError);
                    })
                    .map(Model::new)
                    .subscribe(model -> {
                        this.model = model;
                        this.model.areaNameDirty = true;
                        this.model.areaDescriptionNameDirty = true;
                        refreshCheckedAreaScope();
                        refreshAreaName();
                        refreshAreaDescriptionName();
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    }, () -> {
                        model = new Model();
                        model.areaSettings.setId(FirebaseInstanceId.getInstance().getId());
                        model.areaSettings.setUserId(CurrentUser.getInstance().getUserId());
                        model.areaSettings.setAreaScopeAsEnum(AreaScope.USER);
                        getLog().d("No current project: userId = %s", model.areaSettings.getUserId());
                        refreshCheckedAreaScope();
                        refreshAreaName();
                        refreshAreaDescriptionName();
                    });
            compositeDisposable.add(disposable);
        } else {
            getLog().d("Current project in memory: userId = %s", model.areaSettings.getUserId());
            refreshCheckedAreaScope();
            refreshAreaName();
            refreshAreaDescriptionName();
        }
    }

    @Override
    protected void onStopOverride() {
        super.onStopOverride();

        compositeDisposable.clear();
    }

    public void onCheckedChangedRadioButtonPublic(boolean isChecked) {
        if (isChecked) {
            if (model.areaSettings.getAreaScopeAsEnum() != AreaScope.PUBLIC) {
                model.areaSettings.setAreaScopeAsEnum(AreaScope.PUBLIC);
                model.areaSettings.setAreaId(null);
                model.areaSettings.setAreaDescriptionId(null);
                model.areaName = null;
                model.areaDescriptionName = null;
                refreshAreaName();
                refreshAreaDescriptionName();
            }
        }
    }

    public void onCheckedChangedRadioButtonUser(boolean isChecked) {
        if (isChecked) {
            if (model.areaSettings.getAreaScopeAsEnum() != AreaScope.USER) {
                model.areaSettings.setAreaScopeAsEnum(AreaScope.USER);
                model.areaSettings.setAreaId(null);
                model.areaSettings.setAreaDescriptionId(null);
                model.areaName = null;
                model.areaDescriptionName = null;
                refreshAreaName();
                refreshAreaDescriptionName();
            }
        }
    }

    public void onClickButtonFindByPlace() {
        getView().onShowAreaFindByPlaceView(model.areaSettings.getAreaScopeAsEnum());
    }

    public void onClickButtonFindByName() {
        getView().onShowAreaFindByNameView(model.areaSettings.getAreaScopeAsEnum());
    }

    public void onClickImageButtonSelectAreaDescription() {
        if (model.areaSettings.getAreaId() == null) throw new IllegalStateException("Area ID is null.");

        getView().onShowAreaDescriptionInAreaListView(model.areaSettings.getAreaScopeAsEnum(),
                                                      model.areaSettings.getAreaId());
    }

    public void onClickButtonStart() {
        if (model.areaSettings.getAreaId() == null) throw new IllegalStateException("Area ID is null.");
        if (model.areaSettings.getAreaDescriptionId() == null) {
            throw new IllegalStateException("Area description ID is null.");
        }

        getView().onUpdateEditButtonEnabled(false);

        visionService.getUserAreaSettingsApi().saveUserCurrentAreaSettings(model.areaSettings);
        getView().onShowArView(model.areaSettings);
    }

    public void onUserAreaSelected(@Nullable String areaId) {
        if ((model.areaSettings.getAreaId() != null && model.areaSettings.getAreaId().equals(areaId)) ||
            (model.areaSettings.getAreaId() == null && areaId == null)) {
            model.areaNameDirty = false;
        } else {
            model.areaSettings.setAreaId(areaId);
            model.areaNameDirty = true;

            onUserAreaDescriptionSelected(null);
        }

        refreshAreaName();
    }

    public void onUserAreaDescriptionSelected(@Nullable String areaDescriptionId) {
        if ((model.areaSettings.getAreaDescriptionId() != null &&
             model.areaSettings.getAreaDescriptionId().equals(areaDescriptionId)) ||
            (model.areaSettings.getAreaDescriptionId() == null && areaDescriptionId == null)) {
            model.areaDescriptionNameDirty = false;
        } else {
            model.areaSettings.setAreaDescriptionId(areaDescriptionId);
            model.areaDescriptionNameDirty = true;
        }

        refreshAreaDescriptionName();
    }

    private void refreshCheckedAreaScope() {
        switch (model.areaSettings.getAreaScopeAsEnum()) {
            case PUBLIC:
                getView().onUpdateRadioGroupChecked(R.id.radio_button_public);
                break;
            case USER:
                getView().onUpdateRadioGroupChecked(R.id.radio_button_user);
                break;
            default:
                throw new IllegalStateException(
                        "Unknown area scope: scope = " + model.areaSettings.getAreaScopeAsEnum());
        }
    }

    private void refreshAreaName() {
        if (model.areaNameDirty) {
            model.areaName = null;
            getView().onUpdateAreaName(null);
            updateActionViews();

            if (model.areaSettings.getAreaId() == null) {
                model.areaNameDirty = false;
            } else {
                Disposable disposable = Maybe.<Area>create(e -> {
                    switch (model.areaSettings.getAreaScopeAsEnum()) {
                        case PUBLIC: {
                            visionService.getPublicAreaApi().findAreaById(
                                    model.areaSettings.getAreaId(), area -> {
                                        if (area == null) {
                                            e.onComplete();
                                        } else {
                                            e.onSuccess(area);
                                        }
                                    }, e::onError);
                            break;
                        }
                        case USER: {
                            visionService.getUserAreaApi().findAreaById(
                                    model.areaSettings.getAreaId(), area -> {
                                        if (area == null) {
                                            e.onComplete();
                                        } else {
                                            e.onSuccess(area);
                                        }
                                    }, e::onError);
                            break;
                        }
                    }
                }).subscribe(area -> {
                    model.areaName = area.getName();
                    model.areaNameDirty = false;
                    getView().onUpdateAreaName(model.areaName);
                    updateActionViews();
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                }, () -> {
                    getLog().e("Entity not found.");
                    getView().onSnackbar(R.string.snackbar_failed);
                });
                compositeDisposable.add(disposable);
            }
        } else {
            getView().onUpdateAreaName(model.areaName);
            updateActionViews();
        }
    }

    private void refreshAreaDescriptionName() {
        if (model.areaDescriptionNameDirty) {
            model.areaDescriptionName = null;
            getView().onUpdateAreaDescriptionName(null);
            updateActionViews();

            if (model.areaSettings.getAreaDescriptionId() == null) {
                model.areaDescriptionNameDirty = false;
            } else {
                Disposable disposable = Maybe.<AreaDescription>create(e -> {
                    switch (model.areaSettings.getAreaScopeAsEnum()) {
                        case PUBLIC: {
                            visionService.getPublicAreaDescriptionApi().findAreaDescriptionById(
                                    model.areaSettings.getAreaDescriptionId(),
                                    areaDescription -> {
                                        if (areaDescription == null) {
                                            e.onComplete();
                                        } else {
                                            e.onSuccess(areaDescription);
                                        }
                                    }, e::onError);
                            break;
                        }
                        case USER: {
                            visionService.getUserAreaDescriptionApi().findAreaDescriptionById(
                                    model.areaSettings.getAreaDescriptionId(),
                                    areaDescription -> {
                                        if (areaDescription == null) {
                                            e.onComplete();
                                        } else {
                                            e.onSuccess(areaDescription);
                                        }
                                    }, e::onError);
                            break;
                        }
                    }
                }).subscribe(areaDescription -> {
                    model.areaDescriptionName = areaDescription.getName();
                    model.areaDescriptionNameDirty = false;
                    getView().onUpdateAreaDescriptionName(model.areaDescriptionName);
                    updateActionViews();
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                }, () -> {
                    getLog().e("Entity not found.");
                    getView().onSnackbar(R.string.snackbar_failed);
                });
                compositeDisposable.add(disposable);
            }
        } else {
            getView().onUpdateAreaDescriptionName(model.areaDescriptionName);
            updateActionViews();
        }
    }

    private void updateActionViews() {
        getView().onUpdateAreaDescriptionPickerEnabled(canPickUserAreaDescription());
        getView().onUpdateEditButtonEnabled(canEdit());
    }

    private boolean canPickUserAreaDescription() {
        return model.areaSettings.getAreaId() != null && model.areaName != null && !model.areaNameDirty;
    }

    private boolean canEdit() {
        return model.areaSettings.getAreaId() != null && model.areaName != null && !model.areaNameDirty &&
               model.areaSettings.getAreaDescriptionId() != null && model.areaDescriptionName != null &&
               !model.areaDescriptionNameDirty;
    }

    @Parcel
    public static final class Model {

        AreaSettings areaSettings;

        String areaName;

        boolean areaNameDirty;

        String areaDescriptionName;

        boolean areaDescriptionNameDirty;

        public Model() {
            this(new AreaSettings());
        }

        public Model(@NonNull AreaSettings areaSettings) {
            this.areaSettings = areaSettings;
        }
    }
}
