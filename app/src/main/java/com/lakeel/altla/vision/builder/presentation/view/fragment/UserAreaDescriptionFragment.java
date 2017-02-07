package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionItemModel;
import com.lakeel.altla.vision.builder.presentation.presenter.UserAreaDescriptionPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionView;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserAreaDescriptionFragment
        extends AbstractFragment<UserAreaDescriptionView, UserAreaDescriptionPresenter>
        implements UserAreaDescriptionView {

    @Inject
    UserAreaDescriptionPresenter presenter;

    @BindView(R.id.text_view_id)
    TextView textViewId;

    @BindView(R.id.text_view_name)
    TextView textViewName;

    private InteractionListener interactionListener;

    @NonNull
    public static UserAreaDescriptionFragment newInstance(@NonNull String areaDescriptionId) {
        UserAreaDescriptionFragment fragment = new UserAreaDescriptionFragment();
        Bundle bundle = UserAreaDescriptionPresenter.createArguments(areaDescriptionId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected UserAreaDescriptionPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserAreaDescriptionView getViewInterface() {
        return this;
    }

    @Override
    protected void onAttachOverride(@NonNull Context context) {
        super.onAttachOverride(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
        interactionListener = InteractionListener.class.cast(context);
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
        return inflater.inflate(R.layout.fragment_user_area_description, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onModelUpdated(@NonNull UserAreaDescriptionItemModel model) {
        textViewId.setText(model.areaDescriptionId);
        textViewName.setText(model.name);
    }

    public interface InteractionListener {

    }
}
