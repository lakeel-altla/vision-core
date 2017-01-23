package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.AreaDescriptionListPresenter;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionListView;
import com.lakeel.altla.vision.builder.presentation.view.adapter.AreaDescriptionModelAdapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class AreaDescriptionListFragment extends Fragment implements AreaDescriptionListView {

    private static final String ARG_CURRENT_AREA_DESCRIPTION_ID = "currentAreaDescriptionId";

    @Inject
    AreaDescriptionListPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private InterationListener interationListener;

    public static AreaDescriptionListFragment newInstance(String currentAreaDescriptionId) {
        AreaDescriptionListFragment fragment = new AreaDescriptionListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CURRENT_AREA_DESCRIPTION_ID, currentAreaDescriptionId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
        interationListener = InterationListener.class.cast(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null) throw new IllegalStateException("Arguments must be not null.");

        String currentAreaDescriptionId = getArguments().getString(ARG_CURRENT_AREA_DESCRIPTION_ID, null);

        presenter.onCreate(currentAreaDescriptionId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_area_description_list, container, false);
        ButterKnife.bind(this, view);

        presenter.onCreateView(this);

        recyclerView.setAdapter(new AreaDescriptionModelAdapter(presenter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void updateItems() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void updateItem(int position) {
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void loadAreaDescription(String areaDescriptionId) {
        interationListener.onLoadAreaDescription(areaDescriptionId);
    }

    @Override
    public void showSnackbar(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }

    public interface InterationListener {

        void onLoadAreaDescription(String areaDescriptionId);
    }
}
