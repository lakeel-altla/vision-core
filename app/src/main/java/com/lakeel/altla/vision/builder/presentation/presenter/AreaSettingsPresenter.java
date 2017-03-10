package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.SceneBuildModel;
import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsView;
import com.lakeel.altla.vision.domain.helper.CurrentDeviceResolver;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.AreaScope;
import com.lakeel.altla.vision.domain.model.CurrentAreaSettings;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserCurrentAreaSettingsUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserCurrentAreaSettingsUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcel;
import org.parceler.Parcels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class AreaSettingsPresenter extends BasePresenter<AreaSettingsView> {

    private static final String STATE_MODEL = "model";

    @Inject
    FindUserCurrentAreaSettingsUseCase findUserCurrentAreaSettingsUseCase;

    @Inject
    SaveUserCurrentAreaSettingsUseCase saveUserCurrentAreaSettingsUseCase;

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    @Inject
    FindUserAreaDescriptionUseCase findUserAreaDescriptionUseCase;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    CurrentDeviceResolver currentDeviceResolver;

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
            Disposable disposable = findUserCurrentAreaSettingsUseCase
                    .execute()
                    .map(Model::new)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(model -> {
                        this.model = model;
                        this.model.areaNameDirty = true;
                        this.model.areaDescriptionNameDirty = true;
                        refreshCheckedAreaType();
                        refreshAreaName();
                        refreshAreaDescriptionName();
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    }, () -> {
                        model = new Model();
                        model.currentAreaSettings.setId(currentDeviceResolver.getInstanceId());
                        model.currentAreaSettings.setUserId(currentUserResolver.getUserId());
                        model.currentAreaSettings.setAreaScopeAsEnum(AreaScope.USER);
                        getLog().d("No current project: userId = %s", model.currentAreaSettings.getUserId());
                        refreshCheckedAreaType();
                        refreshAreaName();
                        refreshAreaDescriptionName();
                    });
            manageDisposable(disposable);
        } else {
            getLog().d("Current project in memory: userId = %s", model.currentAreaSettings.getUserId());
            refreshCheckedAreaType();
            refreshAreaName();
            refreshAreaDescriptionName();
        }
    }

    public void onCheckedChangedRadioButtonPublic(boolean isChecked) {
        if (isChecked) {
            if (model.currentAreaSettings.getAreaScopeAsEnum() != AreaScope.PUBLIC) {
                model.currentAreaSettings.setAreaScopeAsEnum(AreaScope.PUBLIC);
                model.currentAreaSettings.setAreaId(null);
                model.currentAreaSettings.setAreaDescriptionId(null);
                refreshAreaName();
                refreshAreaDescriptionName();
            }
        }
    }

    public void onCheckedChangedRadioButtonUser(boolean isChecked) {
        if (isChecked) {
            if (model.currentAreaSettings.getAreaScopeAsEnum() != AreaScope.USER) {
                model.currentAreaSettings.setAreaScopeAsEnum(AreaScope.USER);
                model.currentAreaSettings.setAreaId(null);
                model.currentAreaSettings.setAreaDescriptionId(null);
                refreshAreaName();
                refreshAreaDescriptionName();
            }
        }
    }

    public void onClickButtonFindByPlace() {
        getView().onShowAreaFindByPlaceView(model.currentAreaSettings.getAreaScopeAsEnum());
    }

    public void onClickButtonFindByName() {
        getView().onShowAreaFindByNameView(model.currentAreaSettings.getAreaScopeAsEnum());
    }

    public void onClickImageButtonSelectAreaDescription() {
        getView().onShowUserAreaDescriptionListInAreaView(model.currentAreaSettings.getAreaId());
    }

    public void onClickButtonEdit() {
        getView().onUpdateEditButtonEnabled(false);

        Disposable disposable = saveUserCurrentAreaSettingsUseCase
                .execute(model.currentAreaSettings)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    SceneBuildModel sceneBuildModel = new SceneBuildModel(
                            model.currentAreaSettings.getAreaId(), model.currentAreaSettings.getAreaDescriptionId());
                    getView().onShowUserSceneEditView(sceneBuildModel);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onUserAreaSelected(@Nullable String areaId) {
        if ((model.currentAreaSettings.getAreaId() != null && model.currentAreaSettings.getAreaId().equals(areaId)) ||
            (model.currentAreaSettings.getAreaId() == null && areaId == null)) {
            model.areaNameDirty = false;
        } else {
            model.currentAreaSettings.setAreaId(areaId);
            model.areaNameDirty = true;

            onUserAreaDescriptionSelected(null);
        }

        refreshAreaName();
    }

    public void onUserAreaDescriptionSelected(@Nullable String areaDescriptionId) {
        if ((model.currentAreaSettings.getAreaDescriptionId() != null &&
             model.currentAreaSettings.getAreaDescriptionId().equals(areaDescriptionId)) ||
            (model.currentAreaSettings.getAreaDescriptionId() == null && areaDescriptionId == null)) {
            model.areaDescriptionNameDirty = false;
        } else {
            model.currentAreaSettings.setAreaDescriptionId(areaDescriptionId);
            model.areaDescriptionNameDirty = true;
        }

        refreshAreaDescriptionName();
    }

    private void refreshCheckedAreaType() {
        switch (model.currentAreaSettings.getAreaScopeAsEnum()) {
            case PUBLIC:
                getView().onUpdateRadioGroupChecked(R.id.radio_button_public);
                break;
            case USER:
                getView().onUpdateRadioGroupChecked(R.id.radio_button_user);
                break;
            default:
                throw new IllegalStateException(
                        "Unknown area scope: scope = " + model.currentAreaSettings.getAreaScopeAsEnum());
        }
    }

    private void refreshAreaName() {
        if (model.areaNameDirty) {
            model.areaName = null;
            getView().onUpdateAreaName(null);
            updateActionViews();

            if (model.currentAreaSettings.getAreaId() == null) {
                model.areaNameDirty = false;
            } else {
                Disposable disposable = findUserAreaUseCase
                        .execute(model.currentAreaSettings.getAreaId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(area -> {
                            model.areaName = area.getName();
                            model.areaNameDirty = false;
                            getView().onUpdateAreaName(model.areaName);
                            updateActionViews();
                        }, e -> {
                            getLog().e("Failed.", e);
                        });
                manageDisposable(disposable);
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

            if (model.currentAreaSettings.getAreaDescriptionId() == null) {
                model.areaDescriptionNameDirty = false;
            } else {
                Disposable disposable = findUserAreaDescriptionUseCase
                        .execute(model.currentAreaSettings.getAreaDescriptionId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(areaDescription -> {
                            model.areaDescriptionName = areaDescription.getName();
                            model.areaDescriptionNameDirty = false;
                            getView().onUpdateAreaDescriptionName(model.areaDescriptionName);
                            updateActionViews();
                        }, e -> {
                            getLog().e("Failed.", e);
                        });
                manageDisposable(disposable);
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
        return model.currentAreaSettings.getAreaId() != null && model.areaName != null && !model.areaNameDirty;
    }

    private boolean canEdit() {
        return model.currentAreaSettings.getAreaId() != null && model.areaName != null && !model.areaNameDirty &&
               model.currentAreaSettings.getAreaDescriptionId() != null && model.areaDescriptionName != null &&
               !model.areaDescriptionNameDirty;
    }

    @Parcel
    public static final class Model {

        CurrentAreaSettings currentAreaSettings;

        String areaName;

        boolean areaNameDirty;

        String areaDescriptionName;

        boolean areaDescriptionNameDirty;

        public Model() {
            this(new CurrentAreaSettings());
        }

        public Model(@NonNull CurrentAreaSettings currentAreaSettings) {
            this.currentAreaSettings = currentAreaSettings;
        }
    }
}
