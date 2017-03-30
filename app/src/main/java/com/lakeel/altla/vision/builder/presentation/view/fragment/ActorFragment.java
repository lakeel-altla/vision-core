package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.helper.DateFormatHelper;
import com.lakeel.altla.vision.builder.presentation.presenter.ActorPresenter;
import com.lakeel.altla.vision.builder.presentation.view.ActorView;
import com.lakeel.altla.vision.model.AreaScope;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class ActorFragment extends AbstractFragment<ActorView, ActorPresenter>
        implements ActorView {

    @Inject
    ActorPresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

    @BindView(R.id.text_view_name)
    TextView textViewName;

    @BindView(R.id.text_view_created_at)
    TextView textViewCreatedAt;

    @BindView(R.id.text_view_updated_at)
    TextView textViewUpdatedAt;

    private InteractionListener interactionListener;

    @NonNull
    public static ActorFragment newInstance(@NonNull AreaScope areaScope, @NonNull String actorId) {
        ActorFragment fragment = new ActorFragment();
        Bundle bundle = ActorPresenter.createArguments(areaScope, actorId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected ActorPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected ActorView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_actor, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onUpdateName(@NonNull String name) {
        textViewName.setText(name);
    }

    @Override
    public void onUpdateCreatedAt(long createdAt) {
        textViewCreatedAt.setText(DateFormatHelper.format(getContext(), createdAt));
    }

    @Override
    public void onUpdateUpdatedAt(long updatedAt) {
        textViewUpdatedAt.setText(DateFormatHelper.format(getContext(), updatedAt));
    }

    @Override
    public void onUpdateMainMenuVisible(boolean visible) {
        interactionListener.onUpdateMainMenuVisible(visible);
    }

    @Override
    public void onCloseView() {
        interactionListener.onCloseActorView();
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    public void onUpdateActor(@NonNull AreaScope areaScope, @Nullable String actorId) {
        presenter.onUpdateActor(areaScope, actorId);
    }

    @OnClick(R.id.image_button_close)
    void onClickImageButtonClose() {
        presenter.onClickImageButtonClose();
    }

    public interface InteractionListener {

        void onUpdateMainMenuVisible(boolean visible);

        void onCloseActorView();
    }
}
