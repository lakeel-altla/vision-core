package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.model.AreaSettingsModel;
import com.lakeel.altla.vision.builder.presentation.presenter.AreaSettingsContainerPresenter;
import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsContainerView;
import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.model.AreaDescription;
import com.lakeel.altla.vision.model.AreaScope;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;

public final class AreaSettingsContainerFragment
        extends AbstractFragment<AreaSettingsContainerView, AreaSettingsContainerPresenter>
        implements AreaSettingsContainerView,
                   AreaSettingsFragment.InteractionListener,
                   AreaModeFragment.InteractionListener,
                   AreaFindFragment.InteractionListener,
                   AreaByPlaceListFragment.InteractionListener,
                   AreaDescriptionByAreaListFragment.InteractionListener {

    @Inject
    AreaSettingsContainerPresenter presenter;

    @Inject
    AppCompatActivity activity;

    @Inject
    GoogleApiClient googleApiClient;

    private InteractionListener interactionListener;

    @NonNull
    public static AreaSettingsContainerFragment newInstance() {
        return new AreaSettingsContainerFragment();
    }

    @Override
    protected AreaSettingsContainerPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected AreaSettingsContainerView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_area_settings_container, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onShowAreaSettingsView(@NonNull AreaSettingsModel model) {
        replaceFragment(AreaSettingsFragment.newInstance(model));
    }

    @Override
    public void onCloseAreaSettingsView() {
        interactionListener.onUpdateAreaSettingsVisible(false);
        interactionListener.onUpdateMainMenuVisible(true);
    }

    @Override
    public void onShowAreaModeView(@NonNull AreaSettingsModel model) {
        replaceFragmentAndAddToBackStack(AreaModeFragment.newInstance(model.getAreaScope()));
    }

    @Override
    public void onCloseAreaModeView() {
        backFragment();
    }

    @Override
    public void onShowAreaFindView(@NonNull AreaSettingsModel model) {
        replaceFragmentAndAddToBackStack(AreaFindFragment.newInstance());
    }

    @Override
    public void onShowAreaByPlaceListView(@NonNull AreaScope areaScope, @NonNull Place place) {
        replaceFragmentAndAddToBackStack(AreaByPlaceListFragment.newInstance(areaScope, place));
    }

    @Override
    public void onAreaModeSelected(@NonNull AreaScope areaScope) {
        presenter.onAreaModeSelected(areaScope);
    }

    @Override
    public void onPlaceSelected(@NonNull Place place) {
        presenter.onPlaceSelected(place);
    }

    @Override
    public void onCloseAreaFindView() {
        backFragment();
    }

    @Override
    public void onAreaSelected(@NonNull Area area) {
        presenter.onAreaSelected(area);
    }

    @Override
    public void onBackToAreaFindView() {
        backFragment();
    }

    @Override
    public void onCloseAreaByPlaceListView() {
        getChildFragmentManager().popBackStack(AreaFindFragment.class.getName(),
                                               FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onShowAreaDescriptionByAreaListView(@NonNull AreaSettingsModel model) {
        if (model.getArea() == null) {
            throw new IllegalArgumentException("Invalid argument 'model': 'area' is null.");
        }

        replaceFragmentAndAddToBackStack(
                AreaDescriptionByAreaListFragment.newInstance(model.getAreaScope(), model.getArea()));
    }

    @Override
    public void onAreaSettingsSelected(@NonNull AreaSettingsModel model) {
        interactionListener.onAreaSettingsSelected(model);
    }

    @Override
    public void onAreaDescriptionSelected(@NonNull AreaDescription areaDescription) {
        presenter.onAreaDescriptionSelected(areaDescription);
    }

    @Override
    public void onCloseAreaDescriptionByAreaListView() {
        backFragment();
    }

    private void replaceFragmentAndAddToBackStack(@NonNull Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                                 .addToBackStack(fragment.getClass().getName())
                                 .replace(R.id.fragment_container, fragment, fragment.getClass().getName())
                                 .commit();
    }

    private void replaceFragment(@NonNull Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                                 .replace(R.id.fragment_container, fragment, fragment.getClass().getName())
                                 .commit();
    }

    private void backFragment() {
        if (0 < getChildFragmentManager().getBackStackEntryCount()) {
            getChildFragmentManager().popBackStack();
        }
    }

    public interface InteractionListener {

        void onUpdateAreaSettingsVisible(boolean visible);

        void onUpdateMainMenuVisible(boolean visible);

        void onAreaSettingsSelected(@NonNull AreaSettingsModel model);
    }
}
