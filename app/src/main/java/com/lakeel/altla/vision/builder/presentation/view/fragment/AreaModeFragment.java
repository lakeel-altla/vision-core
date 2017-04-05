package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.AreaModePresenter;
import com.lakeel.altla.vision.builder.presentation.view.AreaModeView;
import com.lakeel.altla.vision.model.Scope;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public final class AreaModeFragment extends AbstractFragment<AreaModeView, AreaModePresenter>
        implements AreaModeView {

    @Inject
    AreaModePresenter presenter;

    @BindView(R.id.radio_group_scope)
    RadioGroup radioGroupScope;

    private InteractionListener interactionListener;

    @NonNull
    public static AreaModeFragment newInstance(@NonNull Scope scope) {
        AreaModeFragment fragment =new AreaModeFragment();
        Bundle bundle = AreaModePresenter.createArguments(scope);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected AreaModePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected AreaModeView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_area_mode, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onUpdateRadioGroupScopeChecked(@IdRes int checkedId) {
        radioGroupScope.check(checkedId);
    }

    @Override
    public void onAreaModeSelected(@NonNull Scope scope) {
        interactionListener.onAreaModeSelected(scope);
    }

    @Override
    public void onCloseView() {
        interactionListener.onCloseAreaModeView();
    }

    @OnClick(R.id.button_select)
    void onClickButtonSelect() {
        presenter.onClickButtonSelect();
    }

    @OnClick(R.id.image_button_close)
    void onClickButtonClose() {
        presenter.onClickButtonClose();
    }

    @OnCheckedChanged(R.id.radio_button_public)
    void onCheckedChangedRadioButtonPublic(CompoundButton buttonView, boolean checked) {
        presenter.onCheckedChangedRadioButtonPublic(checked);
    }

    @OnCheckedChanged(R.id.radio_button_user)
    void onCheckedChangedRadioButtonUser(CompoundButton buttonView, boolean checked) {
        presenter.onCheckedChangedRadioButtonUser(checked);
    }

    public interface InteractionListener {

        void onAreaModeSelected(@NonNull Scope scope);

        void onCloseAreaModeView();
    }
}
