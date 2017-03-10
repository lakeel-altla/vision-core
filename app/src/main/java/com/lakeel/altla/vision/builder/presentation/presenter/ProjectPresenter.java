package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.SceneBuildModel;
import com.lakeel.altla.vision.builder.presentation.view.ProjectView;
import com.lakeel.altla.vision.domain.helper.CurrentDeviceResolver;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.CurrentProject;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserCurrentProjectUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserCurrentProjectUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcel;
import org.parceler.Parcels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class ProjectPresenter extends BasePresenter<ProjectView> {

    private static final String STATE_MODEL = "model";

    @Inject
    FindUserCurrentProjectUseCase findUserCurrentProjectUseCase;

    @Inject
    SaveUserCurrentProjectUseCase saveUserCurrentProjectUseCase;

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
    public ProjectPresenter() {
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
            Disposable disposable = findUserCurrentProjectUseCase
                    .execute()
                    .map(Model::new)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(model -> {
                        this.model = model;
                        this.model.areaNameDirty = true;
                        this.model.areaDescriptionNameDirty = true;
                        refreshAreaName();
                        refreshAreaDescriptionName();
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    }, () -> {
                        model = new Model();
                        model.currentProject.setId(currentDeviceResolver.getInstanceId());
                        model.currentProject.setUserId(currentUserResolver.getUserId());
                        getLog().d("No current project: userId = %s", model.currentProject.getUserId());
                        refreshAreaName();
                        refreshAreaDescriptionName();
                    });
            manageDisposable(disposable);
        } else {
            getLog().d("Current project in memory: userId = %s", model.currentProject.getUserId());
            refreshAreaName();
            refreshAreaDescriptionName();
        }
    }

    public void onClickImageButtonSelectArea() {
        getView().onShowUserAreaListView();
    }

    public void onClickImageButtonSelectAreaDescription() {
        getView().onShowUserAreaDescriptionListInAreaView(model.currentProject.getAreaId());
    }

    public void onClickButtonEdit() {
        getView().onUpdateEditButtonEnabled(false);

        Disposable disposable = saveUserCurrentProjectUseCase
                .execute(model.currentProject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    SceneBuildModel sceneBuildModel = new SceneBuildModel(model.currentProject.getAreaId(),
                                                                          model.currentProject.getAreaDescriptionId());
                    getView().onShowUserSceneEditView(sceneBuildModel);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onUserAreaSelected(@Nullable String areaId) {
        if ((model.currentProject.getAreaId() != null && model.currentProject.getAreaId().equals(areaId)) ||
            (model.currentProject.getAreaId() == null && areaId == null)) {
            model.areaNameDirty = false;
        } else {
            model.currentProject.setAreaId(areaId);
            model.areaNameDirty = true;

            onUserAreaDescriptionSelected(null);
        }

        refreshAreaName();
    }

    public void onUserAreaDescriptionSelected(@Nullable String areaDescriptionId) {
        if ((model.currentProject.getAreaDescriptionId() != null &&
             model.currentProject.getAreaDescriptionId().equals(areaDescriptionId)) ||
            (model.currentProject.getAreaDescriptionId() == null && areaDescriptionId == null)) {
            model.areaDescriptionNameDirty = false;
        } else {
            model.currentProject.setAreaDescriptionId(areaDescriptionId);
            model.areaDescriptionNameDirty = true;
        }

        refreshAreaDescriptionName();
    }

    private void refreshAreaName() {
        if (model.areaNameDirty) {
            model.areaName = null;
            getView().onUpdateAreaName(null);
            updateActionViews();

            if (model.currentProject.getAreaId() == null) {
                model.areaNameDirty = false;
            } else {
                Disposable disposable = findUserAreaUseCase
                        .execute(model.currentProject.getAreaId())
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

            if (model.currentProject.getAreaDescriptionId() == null) {
                model.areaDescriptionNameDirty = false;
            } else {
                Disposable disposable = findUserAreaDescriptionUseCase
                        .execute(model.currentProject.getAreaDescriptionId())
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
        return model.currentProject.getAreaId() != null && model.areaName != null && !model.areaNameDirty;
    }

    private boolean canPickUserScene() {
        return model.currentProject.getAreaId() != null && model.areaName != null && !model.areaNameDirty;
    }

    private boolean canEdit() {
        return model.currentProject.getAreaId() != null && model.areaName != null && !model.areaNameDirty &&
               model.currentProject.getAreaDescriptionId() != null && model.areaDescriptionName != null &&
               !model.areaDescriptionNameDirty;
    }

    @Parcel
    public static final class Model {

        CurrentProject currentProject;

        String areaName;

        boolean areaNameDirty;

        String areaDescriptionName;

        boolean areaDescriptionNameDirty;

        public Model() {
            this(new CurrentProject());
        }

        public Model(@NonNull CurrentProject currentProject) {
            this.currentProject = currentProject;
        }
    }
}
