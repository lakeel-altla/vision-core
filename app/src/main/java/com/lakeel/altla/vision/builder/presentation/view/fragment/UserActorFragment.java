package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.UserActorPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserActorView;
import com.lakeel.altla.vision.builder.presentation.helper.DateFormatHelper;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserActorFragment extends AbstractFragment<UserActorView, UserActorPresenter>
        implements UserActorView {

    private static final String FORMAT_VECTOR3 = "{ %f, %f, %f }";

    private static final String FORMAT_VECTOR4 = "{ %f, %f, %f, %f }";

    @Inject
    UserActorPresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

    @BindView(R.id.text_view_id)
    TextView textViewId;

    @BindView(R.id.text_view_name)
    TextView textViewName;

    @BindView(R.id.text_view_position)
    TextView textViewPosition;

    @BindView(R.id.text_view_orientation)
    TextView textViewOrientation;

    @BindView(R.id.text_view_scale)
    TextView textViewScale;

    @BindView(R.id.text_view_created_at)
    TextView textViewCreatedAt;

    @BindView(R.id.text_view_updated_at)
    TextView textViewUpdatedAt;

    private InteractionListener interactionListener;

    @NonNull
    public static UserActorFragment newInstance(@NonNull String sceneId, @NonNull String actorId) {
        UserActorFragment fragment = new UserActorFragment();
        Bundle bundle = UserActorPresenter.createArguments(sceneId, actorId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected UserActorPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserActorView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_user_actor, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_actor, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                presenter.onEdit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onUpdateTitle(@Nullable String title) {
        getActivity().setTitle(title);
    }

    @Override
    public void onUpdateActorId(@NonNull String actorId) {
        textViewId.setText(actorId);
    }

    @Override
    public void onUpdateName(@NonNull String name) {
        textViewName.setText(name);
    }

    @Override
    public void onUpdatePosition(double x, double y, double z) {
        textViewPosition.setText(String.format(Locale.getDefault(), FORMAT_VECTOR3, x, y, z));
    }

    @Override
    public void onUpdateOrientation(double x, double y, double z, double w) {
        textViewOrientation.setText(String.format(Locale.getDefault(), FORMAT_VECTOR4, x, y, z, w));
    }

    @Override
    public void onUpdateScale(double x, double y, double z) {
        textViewScale.setText(String.format(Locale.getDefault(), FORMAT_VECTOR3, x, y, z));
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
    public void onShowUserActorEditView(@NonNull String sceneId, @NonNull String actorId) {
        interactionListener.onShowUserActorEditView(sceneId, actorId);
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    public interface InteractionListener {

        void onShowUserActorEditView(@NonNull String sceneId, @NonNull String actorId);
    }
}
