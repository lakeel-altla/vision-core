package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.UserAreaDescriptionListPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionListView;
import com.lakeel.altla.vision.builder.presentation.view.adapter.UserAreaDescriptionListAdapter;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserAreaDescriptionListFragment
        extends AbstractFragment<UserAreaDescriptionListView, UserAreaDescriptionListPresenter>
        implements UserAreaDescriptionListView {

    @Inject
    UserAreaDescriptionListPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private InterationListener interationListener;

    public static UserAreaDescriptionListFragment newInstance() {
        return new UserAreaDescriptionListFragment();
    }

    @Override
    protected UserAreaDescriptionListPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserAreaDescriptionListView getViewInterface() {
        return this;
    }

    @Override
    protected void onAttachOverride(@NonNull Context context) {
        super.onAttachOverride(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
        interationListener = InterationListener.class.cast(context);
    }

    @Override
    protected void onDetachOverride() {
        super.onDetachOverride();

        interationListener = null;
    }

    @Nullable
    @Override
    protected View onCreateViewCore(LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_area_description_list, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        recyclerView.setAdapter(new UserAreaDescriptionListAdapter(presenter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onItemsUpdated() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onItemInserted(int position) {
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onShowUserAreaDescriptionScenesView(@NonNull String areaDescriptionId) {
        interationListener.onShowUserAreaDescriptionScenesView(areaDescriptionId);
    }

    public interface InterationListener {

        void onShowUserAreaDescriptionScenesView(@NonNull String areaDescriptionId);
    }
}
