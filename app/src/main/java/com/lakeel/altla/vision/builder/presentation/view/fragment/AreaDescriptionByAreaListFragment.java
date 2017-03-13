package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.AreaDescriptionByAreaListPresenter;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionByAreaListView;
import com.lakeel.altla.vision.builder.presentation.view.adapter.AreaDescriptionByAreaListAdapter;
import com.lakeel.altla.vision.domain.model.AreaScope;
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

public final class AreaDescriptionByAreaListFragment
        extends AbstractFragment<AreaDescriptionByAreaListView, AreaDescriptionByAreaListPresenter>
        implements AreaDescriptionByAreaListView {

    @Inject
    AreaDescriptionByAreaListPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private InteractionListener interactionListener;

    @NonNull
    public static AreaDescriptionByAreaListFragment newInstance(@NonNull AreaScope areaScope, @NonNull String areaId) {
        AreaDescriptionByAreaListFragment fragment = new AreaDescriptionByAreaListFragment();
        Bundle bundle = AreaDescriptionByAreaListPresenter.createArguments(areaScope, areaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected AreaDescriptionByAreaListPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected AreaDescriptionByAreaListView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_area_description_by_area_list, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        recyclerView.setAdapter(new AreaDescriptionByAreaListAdapter(presenter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getActivity().setTitle(R.string.title_user_area_description_list_in_area);
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
    public void onItemSelected(@NonNull String areaDescriptionId) {
        // TODO: import AD
        interactionListener.onUserAreaDescriptionSelected(areaDescriptionId);
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }

    public interface InteractionListener {

        void onUserAreaDescriptionSelected(@NonNull String areaDescriptionId);
    }
}
