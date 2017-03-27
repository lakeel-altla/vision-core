package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.AreaSettingsModel;
import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsView;
import com.lakeel.altla.vision.model.AreaScope;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public final class AreaSettingsPresenter extends BasePresenter<AreaSettingsView> {

    private static final String ARG_MODEL = "model";

    private AreaSettingsModel model;

    @Inject
    public AreaSettingsPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull AreaSettingsModel model) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_MODEL, Parcels.wrap(model));
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        model = Parcels.unwrap(arguments.getParcelable(ARG_MODEL));
        if (model == null) {
            throw new IllegalArgumentException(String.format("Argument '%s' is required.", ARG_MODEL));
        }
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        int resId = (model.getAreaScope() == AreaScope.PUBLIC ? R.string.label_area_mode_public :
                R.string.label_area_mode_user);
        getView().onUpdateAreaMode(resId);
        getView().onUpdateAreaName(model.getArea() == null ? null : model.getArea().getName());
        getView().onUpdateAreaDescriptionName(model.getAreaDescription() == null ? null :
                                                      model.getAreaDescription().getName());

        getView().onUpdateButtonSelectAreaDescriptionEnabled(model.getArea() != null);
    }

    public void onClickButtonClose() {
        getView().onCloseView();
    }

    public void onClickButtonSelectAreaMode() {
        getView().onShowAreaModeView(model);
    }

    public void onClickButtonSelectArea() {
        getView().onShowAreaFindView(model);
    }

    public void onClickButtonSelectAreaDescription() {
        getView().onShowAreaDescriptionByAreaListView(model);
    }

    public void onClickButtonStart() {
        getView().onAreaSettingsSelected(model);
        getView().onCloseView();
    }
}
