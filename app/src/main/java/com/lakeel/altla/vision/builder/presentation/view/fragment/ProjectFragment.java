package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.ProjectPresenter;
import com.lakeel.altla.vision.builder.presentation.view.ProjectView;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class ProjectFragment extends AbstractFragment<ProjectView, ProjectPresenter> implements ProjectView {

    @Inject
    ProjectPresenter presenter;

    @BindView(R.id.text_view_area_name)
    TextView textViewAreaName;

    @BindView(R.id.text_view_area_description_name)
    TextView textViewAreaDescriptionName;

    @BindView(R.id.text_view_scene_name)
    TextView textViewSceneName;

    @BindView(R.id.image_button_select_area_description)
    ImageButton imageButtonSelectAreaDescription;

    @BindView(R.id.image_button_select_scene)
    ImageButton imageButtonSelectScene;

    @BindView(R.id.button_edit)
    Button buttonStart;

    private InteractionListener interactionListener;

    @NonNull
    public static ProjectFragment newInstance() {
        return new ProjectFragment();
    }

    @Override
    protected ProjectPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected ProjectView getViewInterface() {
        return this;
    }

    @Override
    protected void onAttachOverride(@NonNull Context context) {
        super.onAttachOverride(context);

        interactionListener = InteractionListener.class.cast(context);
        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
    }

    @Override
    protected void onDetachOverride() {
        super.onDetachOverride();

        interactionListener = null;
    }

    @Nullable
    @Override
    protected View onCreateViewCore(LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        getActivity().setTitle(R.string.title_project);
    }

    @Override
    public void onUpdateAreaName(@Nullable String areaName) {
        textViewAreaName.setText(areaName);
    }

    @Override
    public void onUpdateAreaDescriptionName(@Nullable String areaDescriptionName) {
        textViewAreaDescriptionName.setText(areaDescriptionName);
    }

    @Override
    public void onUpdateSceneName(@Nullable String sceneName) {
        textViewSceneName.setText(sceneName);
    }

    @Override
    public void onUpdateAreaDescriptionPickerEnabled(boolean enabled) {
        imageButtonSelectAreaDescription.setEnabled(enabled);
        imageButtonSelectAreaDescription.setColorFilter(resolveImageButtonTint(enabled));
    }

    @Override
    public void onUpdateScenePickerEnabled(boolean enabled) {
        imageButtonSelectScene.setEnabled(enabled);
        imageButtonSelectScene.setColorFilter(resolveImageButtonTint(enabled));
    }

    @Override
    public void onShowUserAreaListView() {
        interactionListener.onShowUserAreaListView();
    }

    @Override
    public void onShowUserAreaDescriptionListInAreaView(@NonNull String areaId) {
        interactionListener.onShowUserAreaDescriptionListInAreaView(areaId);
    }

    @Override
    public void onShowUserSceneListInAreaView(@NonNull String areaId) {
        interactionListener.onShowUserSceneListInAreaView(areaId);
    }

    @Override
    public void onShowUserSceneEditView(@NonNull String sceneId) {
        interactionListener.onShowSceneEditView(sceneId);
    }

    @Override
    public void onUpdateEditButtonEnabled(boolean enabled) {
        buttonStart.setEnabled(enabled);
    }

    @OnClick(R.id.image_button_select_area)
    void onClickImageButtonSelectArea() {
        presenter.onClickImageButtonSelectArea();
    }

    @OnClick(R.id.image_button_select_area_description)
    void onClickImageButtonSelectAreaDescription() {
        presenter.onClickImageButtonSelectAreaDescription();
    }

    @OnClick(R.id.image_button_select_scene)
    void onClickImageButtonSelectScene() {
        presenter.onClickImageButtonSelectScene();
    }

    @OnClick(R.id.button_edit)
    void onClickButtonEdit() {
        presenter.onClickButtonEdit();
    }

    public void onUserAreaSelected(@NonNull String areaId) {
        presenter.onUserAreaSelected(areaId);
    }

    public void onUserAreaDescriptionSelected(@NonNull String areaDescriptionId) {
        presenter.onUserAreaDescriptionSelected(areaDescriptionId);
    }

    public void onUserSceneSelected(@NonNull String sceneId) {
        presenter.onUserSceneSelected(sceneId);
    }

    @ColorInt
    private int resolveImageButtonTint(boolean enabled) {
        int resId = enabled ? R.color.tint_image_button_enabled : R.color.tint_image_button_disabled;
        return getResources().getColor(resId);
    }

    public interface InteractionListener {

        void onShowUserAreaListView();

        void onShowUserAreaDescriptionListInAreaView(@NonNull String areaId);

        void onShowUserSceneListInAreaView(@NonNull String areaId);

        void onShowSceneEditView(@NonNull String sceneId);
    }
}
