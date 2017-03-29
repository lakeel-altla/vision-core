package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsContainerView;
import com.lakeel.altla.vision.model.AreaScope;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public final class AreaSettingsContainerPresenter extends BasePresenter<AreaSettingsContainerView> {

    private boolean initialDisplay;

    @Inject
    public AreaSettingsContainerPresenter() {
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (savedInstanceState == null) {
            initialDisplay = true;
        }
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        if (initialDisplay) {
            getView().onShowAreaSettingsView(AreaScope.USER);
        }
    }
}
