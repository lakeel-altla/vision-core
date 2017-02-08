package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.builder.presentation.model.ProjectModel;
import com.lakeel.altla.vision.builder.presentation.view.ProjectView;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserSceneUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class ProjectPresenter extends BasePresenter<ProjectView> {

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    @Inject
    FindUserAreaDescriptionUseCase findUserAreaDescriptionUseCase;

    @Inject
    FindUserSceneUseCase findUserSceneUseCase;

    private String areaId;

    private String areaDescriptionId;

    private String sceneId;

    private ProjectModel model = new ProjectModel();

    @Inject
    public ProjectPresenter() {
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateViewsDependOnUserAreaEnabled(false);
        getView().onUpdateEditButtonEnabled(false);
    }

    public void onClickImageButtonSelectArea() {
        getView().onShowUserAreaListView();
    }

    public void onClickImageButtonSelectAreaDescription() {
        getView().onShowUserAreaDescriptionListInAreaView(areaId);
    }

    public void onClickImageButtonSelectScene() {
        getView().onShowUserSceneListInAreaView(areaId);
    }

    public void onClickButtonEdit() {
        getView().onShowUserSceneEditView(sceneId);
    }

    public void onUserAreaSelected(@NonNull String areaId) {
        this.areaId = areaId;

        Disposable disposable = findUserAreaUseCase
                .execute(areaId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userArea -> {
                    model.areaName = userArea.name;
                    getView().onModelUpdated(model);
                    getView().onUpdateViewsDependOnUserAreaEnabled(true);
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable);
    }

    public void onUserAreaDescriptionSelected(@NonNull String areaDescriptionId) {
        this.areaDescriptionId = areaDescriptionId;

        Disposable disposable = findUserAreaDescriptionUseCase
                .execute(areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userAreaDescription -> {
                    model.areaDescriptionName = userAreaDescription.name;
                    model.areaDescriptionId = userAreaDescription.areaDescriptionId;
                    getView().onModelUpdated(model);
                    getView().onUpdateEditButtonEnabled(canEdit());
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable);
    }

    public void onUserSceneSelected(@NonNull String sceneId) {
        this.sceneId = sceneId;

        Disposable disposable = findUserSceneUseCase
                .execute(sceneId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userScene -> {
                    model.sceneName = userScene.name;
                    getView().onModelUpdated(model);
                    getView().onUpdateEditButtonEnabled(canEdit());
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable);
    }

    private boolean canEdit() {
        return areaId != null && areaDescriptionId != null && sceneId != null;
    }
}
