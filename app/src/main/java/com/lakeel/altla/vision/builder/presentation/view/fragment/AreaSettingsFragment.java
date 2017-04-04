package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.AreaSettingsPresenter;
import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsView;
import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.model.AreaDescription;
import com.lakeel.altla.vision.model.AreaSettings;
import com.lakeel.altla.vision.model.Scope;
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
import android.widget.Button;
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

    @BindView(R.id.image_button_select_area_description)
    ImageButton imageButtonSelectAreaDescription;

    @BindView(R.id.button_start)
    Button buttonStart;

    private InteractionListener interactionListener;

    @NonNull
    public static AreaSettingsFragment newInstance(@NonNull Scope scope) {
        AreaSettingsFragment fragment = new AreaSettingsFragment();
        Bundle bundle = AreaSettingsPresenter.createArguments(scope);
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
    public void onUpdateButtonStartEnabled(boolean enabled) {
        buttonStart.setEnabled(enabled);
    }

    @Override
    public void onShowAreaSettingsHistoryView() {
        interactionListener.onShowAreaSettingsHistoryView();
    }

    @Override
    public void onShowAreaModeView(@NonNull Scope scope) {
        interactionListener.onShowAreaModeView(scope);
    }

    @Override
    public void onShowAreaFindView(@NonNull Scope scope) {
        interactionListener.onShowAreaFindView(scope);
    }

    @Override
    public void onShowAreaDescriptionByAreaListView(@NonNull Scope scope, @NonNull Area area) {
        interactionListener.onShowAreaDescriptionByAreaListView(scope, area);
    }

    @Override
    public void onUpdateArView(@NonNull String areaSettingsId) {
        interactionListener.onUpdateArView(areaSettingsId);
    }

    @Override
    public void onCloseView() {
        interactionListener.onCloseAreaSettingsView();
    }

    public void onAreaModeSelected(@NonNull Scope scope) {
        presenter.onAreaModeSelected(scope);
    }

    public void onAreaSelected(@NonNull Area area) {
        presenter.onAreaSelected(area);
    }

    public void onAreaDescriptionSelected(@NonNull AreaDescription areaDescription) {
        presenter.onAreaDescriptionSelected(areaDescription);
    }

    public void onAreaSettingsSelected(@NonNull AreaSettings areaSettings,
                                       @NonNull Area area,
                                       @NonNull AreaDescription areaDescription) {
        presenter.onAreaSettingsSelected(areaSettings, area, areaDescription);
    }

    @OnClick(R.id.image_button_close)
    void onClickButtonClose() {
        presenter.onClickButtonClose();
    }

    @OnClick(R.id.image_button_history)
    void onClickButtonHistory() {
        presenter.onClickButtonHistory();
    }

    @OnClick(R.id.image_button_select_area_mode)
    void onClickButtonSelectAreaMode() {
        presenter.onClickButtonSelectAreaMode();
    }

    @OnClick(R.id.image_button_select_area)
    void onClickButtonSelectArea() {
        presenter.onClickButtonSelectArea();
    }

    @OnClick(R.id.image_button_select_area_description)
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

        void onShowAreaSettingsHistoryView();

        void onShowAreaModeView(@NonNull Scope scope);

        void onShowAreaFindView(@NonNull Scope scope);

        void onShowAreaDescriptionByAreaListView(@NonNull Scope scope, @NonNull Area area);

        void onUpdateArView(@NonNull String areaSettingsId);

        void onCloseAreaSettingsView();
    }
}
