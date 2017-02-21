package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.builder.presentation.model.SceneEditModel;
import com.lakeel.altla.vision.builder.presentation.view.ProjectView;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserSceneUseCase;
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
    FindUserAreaUseCase findUserAreaUseCase;

    @Inject
    FindUserAreaDescriptionUseCase findUserAreaDescriptionUseCase;

    @Inject
    FindUserSceneUseCase findUserSceneUseCase;

    private Model model = new Model();

    @Inject
    public ProjectPresenter() {
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (savedInstanceState == null) {
            model = new Model();
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

        if (model.isAreaNameDirty) {
            loadAreaName();
        } else {
            getView().onUpdateAreaName(model.areaName);
            updateActionViews();
        }

        if (model.isAreaDescriptionNameDirty) {
            loadAreaDescriptionName();
        } else {
            getView().onUpdateAreaDescriptionName(model.areaDescriptionName);
            updateActionViews();
        }

        if (model.isSceneNameDirty) {
            loadSceneName();
        } else {
            getView().onUpdateSceneName(model.sceneName);
            updateActionViews();
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
        SceneEditModel sceneEditModel = new SceneEditModel(model.areaId, model.areaDescriptionId, model.sceneId);
        getView().onShowUserSceneEditView(sceneEditModel);
    }

    public void onUserAreaSelected(@NonNull String areaId) {
        model.areaId = areaId;
        model.isAreaNameDirty = true;
        loadAreaName();
    }

    public void onUserAreaDescriptionSelected(@NonNull String areaDescriptionId) {
        model.areaDescriptionId = areaDescriptionId;
        model.isAreaDescriptionNameDirty = true;
        loadAreaDescriptionName();
    }

    public void onUserSceneSelected(@NonNull String sceneId) {
        model.sceneId = sceneId;
        model.isSceneNameDirty = true;
        loadSceneName();
    }

    private void loadAreaName() {
        Disposable disposable = findUserAreaUseCase
                .execute(model.areaId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userArea -> {
                    model.areaName = userArea.name;
                    model.isAreaNameDirty = false;
                    getView().onUpdateAreaName(model.areaName);
                    updateActionViews();
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable);
    }

    private void loadAreaDescriptionName() {
        Disposable disposable = findUserAreaDescriptionUseCase
                .execute(model.areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userAreaDescription -> {
                    model.areaDescriptionName = userAreaDescription.name;
                    model.isAreaDescriptionNameDirty = false;
                    getView().onUpdateAreaDescriptionName(model.areaDescriptionName);
                    updateActionViews();
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable);
    }

    private void loadSceneName() {
        Disposable disposable = findUserSceneUseCase
                .execute(model.sceneId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userScene -> {
                    model.sceneName = userScene.name;
                    model.isSceneNameDirty = false;
                    getView().onUpdateSceneName(model.sceneName);
                    updateActionViews();
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable);
    }

    private void updateActionViews() {
        getView().onUpdateAreaDescriptionPickerEnabled(canPickUserAreaDescription());
        getView().onUpdateScenePickerEnabled(canPickUserScene());
        getView().onUpdateEditButtonEnabled(canEdit());
    }

    private boolean canPickUserAreaDescription() {
        return model.areaId != null;
    }

    private boolean canPickUserScene() {
        return model.areaId != null;
    }

    private boolean canEdit() {
        return model.areaId != null && model.areaDescriptionId != null && model.sceneId != null;
    }

    @Parcel
    public static final class Model {

        String areaId;

        String areaName;

        boolean isAreaNameDirty;

        String areaDescriptionId;

        String areaDescriptionName;

        boolean isAreaDescriptionNameDirty;

        String sceneId;

        String sceneName;

        boolean isSceneNameDirty;
    }
}
