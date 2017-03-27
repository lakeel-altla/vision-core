package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.model.AreaSettingsModel;
import com.lakeel.altla.vision.builder.presentation.presenter.AreaSettingsPresenter;
import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsView;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class AreaSettingsFragment extends AbstractFragment<AreaSettingsView, AreaSettingsPresenter>
        implements AreaSettingsView {

    @Inject
    AreaSettingsPresenter presenter;

    @BindView(R.id.text_view_area_mode)
    TextView textViewAreaMode;

    @BindView(R.id.text_view_area_name)
    TextView textViewAreaName;

    @BindView(R.id.text_view_area_description_name)
    TextView textViewAreaDescriptionName;

    @BindView(R.id.button_select_area_description)
    ImageButton imageButtonSelectAreaDescription;

    private InteractionListener interactionListener;

    @NonNull
    public static AreaSettingsFragment newInstance(@NonNull AreaSettingsModel model) {
        AreaSettingsFragment fragment = new AreaSettingsFragment();
        Bundle bundle = AreaSettingsPresenter.createArguments(model);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected AreaSettingsPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected AreaSettingsView getViewInterface() {
        return this;
    }

    @Override
    protected void onAttachOverride(@NonNull Context context) {
        super.onAttachOverride(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
        interactionListener = InteractionListener.class.cast(getParentFragment());
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
        return inflater.inflate(R.layout.fragment_area_settings, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onUpdateAreaMode(@StringRes int resId) {
        textViewAreaMode.setText(resId);
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
    public void onUpdateButtonSelectAreaDescriptionEnabled(boolean enabled) {
        imageButtonSelectAreaDescription.setEnabled(enabled);
        imageButtonSelectAreaDescription.setColorFilter(resolveImageButtonTint(enabled));
    }

    @Override
    public void onShowAreaModeView(@NonNull AreaSettingsModel model) {
        interactionListener.onShowAreaModeView(model);
    }

    @Override
    public void onShowAreaFindView(@NonNull AreaSettingsModel model) {
        interactionListener.onShowAreaFindView(model);
    }

    @Override
    public void onShowAreaDescriptionByAreaListView(@NonNull AreaSettingsModel model) {
        interactionListener.onShowAreaDescriptionByAreaListView(model);
    }

    @Override
    public void onAreaSettingsSelected(@NonNull AreaSettingsModel model) {
        interactionListener.onAreaSettingsSelected(model);
    }

    @Override
    public void onCloseView() {
        interactionListener.onCloseAreaSettingsView();
    }

    @OnClick(R.id.button_close)
    void onClickButtonClose() {
        presenter.onClickButtonClose();
    }

    @OnClick(R.id.button_select_area_mode)
    void onClickButtonSelectAreaMode() {
        presenter.onClickButtonSelectAreaMode();
    }

    @OnClick(R.id.button_select_area)
    void onClickButtonSelectArea() {
        presenter.onClickButtonSelectArea();
    }

    @OnClick(R.id.button_select_area_description)
    void onClickButtonSelectAreaDescription() {
        presenter.onClickButtonSelectAreaDescription();
    }

    @OnClick(R.id.button_start)
    void onClickButtonStart() {
        presenter.onClickButtonStart();
    }

    @ColorInt
    private int resolveImageButtonTint(boolean enabled) {
        int resId = enabled ? R.color.foreground_overlay : R.color.foreground_overlay_disabled;
        return getResources().getColor(resId);
    }

    public interface InteractionListener {

        void onShowAreaModeView(@NonNull AreaSettingsModel model);

        void onShowAreaFindView(@NonNull AreaSettingsModel model);

        void onShowAreaDescriptionByAreaListView(@NonNull AreaSettingsModel model);

        void onAreaSettingsSelected(@NonNull AreaSettingsModel model);

        void onCloseAreaSettingsView();
    }
}
