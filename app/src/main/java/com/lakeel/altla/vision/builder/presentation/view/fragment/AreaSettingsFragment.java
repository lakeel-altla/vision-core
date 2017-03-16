package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.AreaSettingsPresenter;
import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsView;
import com.lakeel.altla.vision.model.AreaScope;
import com.lakeel.altla.vision.model.AreaSettings;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public final class AreaSettingsFragment extends AbstractFragment<AreaSettingsView, AreaSettingsPresenter>
        implements AreaSettingsView {

    @Inject
    AreaSettingsPresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

    @BindView(R.id.radio_group_area_scope)
    RadioGroup radioGroupType;

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
    public static AreaSettingsFragment newInstance() {
        return new AreaSettingsFragment();
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
        return inflater.inflate(R.layout.fragment_area_settings, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        getActivity().setTitle(R.string.title_project);
    }

    @Override
    public void onUpdateRadioGroupChecked(int checkedId) {
        radioGroupType.check(checkedId);
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
    public void onUpdateAreaDescriptionPickerEnabled(boolean enabled) {
        imageButtonSelectAreaDescription.setEnabled(enabled);
        imageButtonSelectAreaDescription.setColorFilter(resolveImageButtonTint(enabled));
    }

    @Override
    public void onUpdateEditButtonEnabled(boolean enabled) {
        buttonStart.setEnabled(enabled);
    }

    @Override
    public void onShowAreaFindByPlaceView(@NonNull AreaScope areaScope) {
        interactionListener.onShowAreaFindByPlaceView(areaScope);
    }

    @Override
    public void onShowAreaFindByNameView(@NonNull AreaScope areaScope) {
        interactionListener.onShowAreaFindByNameView(areaScope);
    }

    @Override
    public void onShowAreaDescriptionInAreaListView(@NonNull AreaScope areaScope, @NonNull String areaId) {
        interactionListener.onShowAreaDescriptionInAreaListView(areaScope, areaId);
    }

    @Override
    public void onShowArView(@NonNull AreaSettings settings) {
        interactionListener.onShowArView(settings);
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    @OnCheckedChanged(R.id.radio_button_public)
    void onCheckedChangedRadioButtonPublic(CompoundButton buttonView, boolean isChecked) {
        presenter.onCheckedChangedRadioButtonPublic(isChecked);
    }

    @OnCheckedChanged(R.id.radio_button_user)
    void onCheckedChangedRadioButtonUser(CompoundButton buttonView, boolean isChecked) {
        presenter.onCheckedChangedRadioButtonUser(isChecked);
    }

    @OnClick(R.id.button_find_by_place)
    void onClickButtonFindByPlace() {
        presenter.onClickButtonFindByPlace();
    }

    @OnClick(R.id.button_find_by_name)
    void onClickButtonFindByName() {
        presenter.onClickButtonFindByName();
    }

    @OnClick(R.id.image_button_select_area_description)
    void onClickImageButtonSelectAreaDescription() {
        presenter.onClickImageButtonSelectAreaDescription();
    }

    @OnClick(R.id.button_start)
    void onClickButtonStart() {
        presenter.onClickButtonStart();
    }

    public void onUserAreaSelected(@NonNull String areaId) {
        presenter.onUserAreaSelected(areaId);
    }

    public void onUserAreaDescriptionSelected(@NonNull String areaDescriptionId) {
        presenter.onUserAreaDescriptionSelected(areaDescriptionId);
    }

    @ColorInt
    private int resolveImageButtonTint(boolean enabled) {
        int resId = enabled ? R.color.tint_image_button_enabled : R.color.tint_image_button_disabled;
        return getResources().getColor(resId);
    }

    public interface InteractionListener {

        void onShowAreaFindByPlaceView(@NonNull AreaScope areaScope);

        void onShowAreaFindByNameView(@NonNull AreaScope areaScope);

        void onShowAreaDescriptionInAreaListView(@NonNull AreaScope areaScope, @NonNull String areaId);

        void onShowArView(@NonNull AreaSettings settings);
    }
}
