package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.AreaModeView;
import com.lakeel.altla.vision.model.Scope;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public final class AreaModePresenter extends BasePresenter<AreaModeView> {

    private static final String ARG_SCOPE = "scrope";

    private static final String STATE_SCOPE = "scope";

    private Scope initialScope;

    private Scope scope;

    @Inject
    public AreaModePresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull Scope scope) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_SCOPE, Parcels.wrap(scope));
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        initialScope = Parcels.unwrap(arguments.getParcelable(ARG_SCOPE));
        if (initialScope == null) {
            throw new IllegalArgumentException(String.format("Argument '%s' is required.", ARG_SCOPE));
        }

        if (savedInstanceState == null) {
            scope = initialScope;
        } else {
            scope = Parcels.unwrap(savedInstanceState.getParcelable(STATE_SCOPE));
            if (scope == null) {
                throw new IllegalStateException(String.format("State '%s' is required.", STATE_SCOPE));
            }
        }
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        int checkedId = (scope == Scope.PUBLIC) ? R.id.radio_button_public : R.id.radio_button_user;
        getView().onUpdateRadioGroupScopeChecked(checkedId);
    }

    public void onClickButtonSelect() {
        getView().onAreaModeSelected(scope);
        getView().onCloseView();
    }

    public void onClickButtonClose() {
        getView().onCloseView();
    }

    public void onCheckedChangedRadioButtonPublic(boolean checked) {
        if (checked) {
            scope = Scope.PUBLIC;
        }
    }

    public void onCheckedChangedRadioButtonUser(boolean checked) {
        if (checked) {
            scope = Scope.USER;
        }
    }
}
