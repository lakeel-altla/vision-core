package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.SceneBuildModel;
import com.lakeel.altla.vision.builder.presentation.view.ProjectView;
import com.lakeel.altla.vision.domain.helper.CurrentDeviceResolver;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserCurrentProject;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserCurrentProjectUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserSceneUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserCurrentProjectUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcel;
import org.parceler.Parcels;

import android.os.Bundle;
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
    FindUserSceneUseCase findUserSceneUseCase;

    @Inject
    CurrentUserResolver currentUserResolver;

    @Inject
    CurrentDeviceResolver currentDeviceResolver;

    private Model model = new Model();

    @Inject
    public ProjectPresenter() {
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (savedInstanceState == null) {
            model = new Model();
            model.newProject = true;
        } else {
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
        getView().onUpdateScenePickerEnabled(false);
        getView().onUpdateEditButtonEnabled(false);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        if (model.newProject) {
            Disposable disposable = findUserCurrentProjectUseCase
                    .execute()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(userCurrentProject -> {
                        model.areaId = userCurrentProject.areaId;
                        model.areaNameDirty = true;
                        model.areaDescriptionId = userCurrentProject.areaDescriptionId;
                        model.areaDescriptionNameDirty = true;
                        model.sceneId = userCurrentProject.sceneId;
                        model.sceneNameDirty = true;
                        model.newProject = false;
                        refreshAreaName();
                        refreshAreaDescriptionName();
                        refreshSceneName();
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    }, () -> {
                        model.newProject = false;
                        refreshAreaName();
                        refreshAreaDescriptionName();
                        refreshSceneName();
                    });
            manageDisposable(disposable);
        } else {
            refreshAreaName();
            refreshAreaDescriptionName();
            refreshSceneName();
        }
    }

    public void onClickImageButtonSelectArea() {
        getView().onShowUserAreaListView();
    }

    public void onClickImageButtonSelectAreaDescription() {
        getView().onShowUserAreaDescriptionListInAreaView(model.areaId);
    }

    public void onClickImageButtonSelectScene() {
        getView().onShowUserSceneListInAreaView(model.areaId);
    }

    public void onClickButtonEdit() {
        getView().onUpdateEditButtonEnabled(false);

        String userId = currentUserResolver.getUserId();
        String instanceId = currentDeviceResolver.getInstanceId();

        UserCurrentProject userCurrentProject = new UserCurrentProject(userId, instanceId);
        userCurrentProject.areaId = model.areaId;
        userCurrentProject.areaDescriptionId = model.areaDescriptionId;
        userCurrentProject.sceneId = model.sceneId;

        Disposable disposable = saveUserCurrentProjectUseCase
                .execute(userCurrentProject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    SceneBuildModel sceneBuildModel = new SceneBuildModel(model.areaId,
                                                                          model.areaDescriptionId,
                                                                          model.sceneId);
                    getView().onShowUserSceneEditView(sceneBuildModel);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onUserAreaSelected(@Nullable String areaId) {
        if ((model.areaId != null && model.areaId.equals(areaId)) ||
            (model.areaId == null && areaId == null)) {
            model.areaNameDirty = false;
        } else {
            model.areaId = areaId;
            model.areaNameDirty = true;

            onUserAreaDescriptionSelected(null);
            onUserSceneSelected(null);
        }

        refreshAreaName();
    }

    public void onUserAreaDescriptionSelected(@Nullable String areaDescriptionId) {
        if ((model.areaDescriptionId != null && model.areaDescriptionId.equals(areaDescriptionId)) ||
            (model.areaDescriptionId == null && areaDescriptionId == null)) {
            model.areaDescriptionNameDirty = false;
        } else {
            model.areaDescriptionId = areaDescriptionId;
            model.areaDescriptionNameDirty = true;
        }

        refreshAreaDescriptionName();
    }

    public void onUserSceneSelected(@Nullable String sceneId) {
        if ((model.sceneId != null && model.sceneId.equals(sceneId)) ||
            (model.sceneId == null && sceneId == null)) {
            model.sceneNameDirty = false;
        } else {
            model.sceneId = sceneId;
            model.sceneNameDirty = true;
        }

        refreshSceneName();
    }

    private void refreshAreaName() {
        if (model.areaNameDirty) {
            model.areaName = null;
            getView().onUpdateAreaName(null);
            updateActionViews();

            if (model.areaId == null) {
                model.areaNameDirty = false;
            } else {
                Disposable disposable = findUserAreaUseCase
                        .execute(model.areaId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userArea -> {
                            model.areaName = userArea.name;
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

            if (model.areaDescriptionId == null) {
                model.areaDescriptionNameDirty = false;
            } else {
                Disposable disposable = findUserAreaDescriptionUseCase
                        .execute(model.areaDescriptionId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userAreaDescription -> {
                            model.areaDescriptionName = userAreaDescription.name;
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

    private void refreshSceneName() {
        if (model.sceneNameDirty) {
            model.sceneName = null;
            getView().onUpdateSceneName(null);
            updateActionViews();

            if (model.sceneId == null) {
                model.sceneNameDirty = false;
            } else {
                Disposable disposable = findUserSceneUseCase
                        .execute(model.sceneId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userScene -> {
                            model.sceneName = userScene.name;
                            model.sceneNameDirty = false;
                            getView().onUpdateSceneName(model.sceneName);
                            updateActionViews();
                        }, e -> {
                            getLog().e("Failed.", e);
                        });
                manageDisposable(disposable);
            }
        } else {
            getView().onUpdateSceneName(model.sceneName);
            updateActionViews();
        }
    }

    private void updateActionViews() {
        getView().onUpdateAreaDescriptionPickerEnabled(canPickUserAreaDescription());
        getView().onUpdateScenePickerEnabled(canPickUserScene());
        getView().onUpdateEditButtonEnabled(canEdit());
    }

    private boolean canPickUserAreaDescription() {
        return model.areaId != null && model.areaName != null && !model.areaNameDirty;
    }

    private boolean canPickUserScene() {
        return model.areaId != null && model.areaName != null && !model.areaNameDirty;
    }

    private boolean canEdit() {
        return model.areaId != null && model.areaName != null && !model.areaNameDirty &&
               model.areaDescriptionId != null && model.areaDescriptionName != null &&
               !model.areaDescriptionNameDirty &&
               model.sceneId != null && model.sceneName != null && !model.sceneNameDirty;
    }

    @Parcel
    public static final class Model {

        String areaId;

        String areaName;

        boolean areaNameDirty;

        String areaDescriptionId;

        String areaDescriptionName;

        boolean areaDescriptionNameDirty;

        String sceneId;

        String sceneName;

        boolean sceneNameDirty;

        boolean newProject;
    }
}
