package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.UserSceneCreatePresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserSceneCreateView;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public final class UserSceneCreateFragment extends AbstractFragment<UserSceneCreateView, UserSceneCreatePresenter>
        implements UserSceneCreateView {

    @Inject
    UserSceneCreatePresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

    @BindView(R.id.text_input_edit_text_name)
    TextInputEditText textInputEditTextName;

    @BindView(R.id.button_create)
    Button buttonCreate;

    private InteractionListener interactionListener;

    @NonNull
    public static UserSceneCreateFragment newInstance(@NonNull String areaId) {
        UserSceneCreateFragment fragment = new UserSceneCreateFragment();
        Bundle bundle = UserSceneCreatePresenter.createArguments(areaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected UserSceneCreatePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserSceneCreateView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_user_scene_create, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onUpdateButtonCreateEnabled(boolean enabled) {
        buttonCreate.setEnabled(enabled);
    }

    @Override
    public void onCreated() {
        interactionListener.onCloseUserSceneCreateView();
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    @OnTextChanged(value = R.id.text_input_edit_text_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onEditTextNameAfterTextChanged(Editable editable) {
        presenter.onEditTextNameAfterTextChanged(editable.toString());
    }

    @OnClick(R.id.button_create)
    void onClickButtonCreate() {
        presenter.onClickButtonCreate();
    }

    public interface InteractionListener {

        void onCloseUserSceneCreateView();
    }
}
