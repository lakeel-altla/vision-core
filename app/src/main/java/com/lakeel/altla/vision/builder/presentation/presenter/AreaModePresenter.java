package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.AreaModeView;
import com.lakeel.altla.vision.model.AreaScope;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public final class AreaModePresenter extends BasePresenter<AreaModeView> {

    private static final String ARG_AREA_SCOPE_VALUE = "areaScropeValue";

    private static final String STATE_AREA_SCOPE_VALUE = "areaScopeValue";

    private AreaScope initialAreaScope;

    private AreaScope areaScope;

    @Inject
    public AreaModePresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull AreaScope areaScope) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_AREA_SCOPE_VALUE, areaScope.getValue());
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        int initialAreaScopeValue = arguments.getInt(ARG_AREA_SCOPE_VALUE, -1);
        if (initialAreaScopeValue < 0) {
            throw new IllegalArgumentException(String.format("Argument '%s' is required.", ARG_AREA_SCOPE_VALUE));
        }
        initialAreaScope = AreaScope.toAreaScope(initialAreaScopeValue);

        if (savedInstanceState == null) {
            areaScope = initialAreaScope;
        } else {
            int areaScopeValue = savedInstanceState.getInt(STATE_AREA_SCOPE_VALUE, AreaScope.UNKNOWN.getValue());
            AreaScope areaScope = AreaScope.toAreaScope(areaScopeValue);
            if (areaScope == AreaScope.UNKNOWN) {
                areaScope = initialAreaScope;
            }

            this.areaScope = areaScope;
        }
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        int checkedId = (areaScope == AreaScope.PUBLIC) ? R.id.radio_button_public : R.id.radio_button_user;
        getView().onUpdateRadioGroupScopeChecked(checkedId);
    }

    public void onClickButtonSelect() {
        getView().onAreaModeSelected(areaScope);
        getView().onCloseAreaModeView();
    }

    public void onClickButtonClose() {
        getView().onCloseAreaModeView();
    }

    public void onCheckedChangedRadioButtonPublic(boolean checked) {
        if (checked) {
            areaScope = AreaScope.PUBLIC;
        }
    }

    public void onCheckedChangedRadioButtonUser(boolean checked) {
        if (checked) {
            areaScope = AreaScope.USER;
        }
    }
}
