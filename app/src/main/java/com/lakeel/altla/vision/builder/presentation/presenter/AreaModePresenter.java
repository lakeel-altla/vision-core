package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.AreaModeView;
import com.lakeel.altla.vision.model.Scope;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

public final class AreaModePresenter extends BasePresenter<AreaModeView> {

    private static final String ARG_SCOPE_VALUE = "scropeValue";

    private static final String STATE_SCOPE_VALUE = "scopeValue";

    private Scope initialScope;

    private Scope scope;

    @Inject
    public AreaModePresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull Scope scope) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SCOPE_VALUE, scope.getValue());
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        int initialScopeValue = arguments.getInt(ARG_SCOPE_VALUE, -1);
        if (initialScopeValue < 0) {
            throw new IllegalArgumentException(String.format("Argument '%s' is required.", ARG_SCOPE_VALUE));
        }

        initialScope = Scope.toAreaScope(initialScopeValue);
        if (initialScope == Scope.UNKNOWN) throw new IllegalArgumentException("Unknown scope.");

        if (savedInstanceState == null) {
            scope = initialScope;
        } else {
            int scopeValue = savedInstanceState.getInt(STATE_SCOPE_VALUE, -1);
            if (scopeValue < 0) {
                throw new IllegalStateException(String.format("State '%s' is required.", STATE_SCOPE_VALUE));
            }

            scope = Scope.toAreaScope(scopeValue);
            if (scope == Scope.UNKNOWN) throw new IllegalArgumentException("Unknown scope.");
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
