package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.UserAssetImageListPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserAssetImageListView;
import com.lakeel.altla.vision.builder.presentation.view.adapter.UserAssetImageListAdapter;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class UserAssetImageListFragment
        extends AbstractFragment<UserAssetImageListView, UserAssetImageListPresenter>
        implements UserAssetImageListView {

    @Inject
    UserAssetImageListPresenter presenter;

    @BindView(R.id.image_button_expand)
    ImageButton imageButtonExpand;

    @BindView(R.id.image_button_collapse)
    ImageButton imageButtonCollapse;

    @BindView(R.id.view_group_content)
    ViewGroup viewGroupContent;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @NonNull
    public static UserAssetImageListFragment newInstance() {
        return new UserAssetImageListFragment();
    }

    @Override
    protected UserAssetImageListPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserAssetImageListView getViewInterface() {
        return this;
    }

    @Override
    protected void onAttachOverride(@NonNull Context context) {
        super.onAttachOverride(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    protected View onCreateViewCore(LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_asset_image_list, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        recyclerView.setAdapter(new UserAssetImageListAdapter(presenter, getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));
    }

    @Override
    public void onUpdateImageButtonExpandVisible(boolean visible) {
        imageButtonExpand.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onUpdateImageButtonCollapseVisible(boolean visible) {
        imageButtonCollapse.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onUpdateContentVisible(boolean visible) {
        viewGroupContent.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemInserted(int position) {
        recyclerView.getAdapter().notifyItemInserted(position);
    }

    @Override
    public void onItemChanged(int position) {
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void onItemRemoved(int position) {
        recyclerView.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onDataSetChanged() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.image_button_expand)
    void onClickImageButtonExpand() {
        presenter.onClickImageButtonExpand();
    }

    @OnClick(R.id.image_button_collapse)
    void onClickImageButtonCollapse() {
        presenter.onClickImageButtonCollapse();
    }
}
