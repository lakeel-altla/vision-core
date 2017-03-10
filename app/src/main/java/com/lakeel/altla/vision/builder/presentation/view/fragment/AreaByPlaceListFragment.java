package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.AreaByPlaceListPresenter;
import com.lakeel.altla.vision.builder.presentation.view.AreaByPlaceListView;
import com.lakeel.altla.vision.builder.presentation.view.adapter.AreaByPlaceListAdapter;
import com.lakeel.altla.vision.domain.model.AreaScope;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class AreaByPlaceListFragment extends AbstractFragment<AreaByPlaceListView, AreaByPlaceListPresenter>
        implements AreaByPlaceListView {

    private static final int REQUEST_CODE_PLACE_PICKER = 1;

    @Inject
    AreaByPlaceListPresenter presenter;

    @Inject
    AppCompatActivity activity;

    @Inject
    GoogleApiClient googleApiClient;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private InteractionListener interactionListener;

    @NonNull
    public static AreaByPlaceListFragment newInstance(@NonNull AreaScope areaScope) {
        AreaByPlaceListFragment fragment = new AreaByPlaceListFragment();
        Bundle bundle = AreaByPlaceListPresenter.createArguments(areaScope);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public AreaByPlaceListPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected AreaByPlaceListView getViewInterface() {
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
        return inflater.inflate(R.layout.fragment_area_by_place_list, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        recyclerView.setAdapter(new AreaByPlaceListAdapter(presenter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getActivity().setTitle(R.string.title_select_area);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_area_by_place_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pick_place:
                presenter.onActionPickPlace();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PLACE_PICKER) {
            // TODO: renable the action to pick a place.

            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(getContext(), data);
                presenter.onPlacePicked(place);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
    public void onItemSelected(String areaId) {
        interactionListener.onUserAreaSelected(areaId);
    }

    @Override
    public void onShowPlacePicker() {
        if (googleApiClient.isConnected()) {
            // TODO: disable the action to pick a place.
            try {
                Intent intent = new PlacePicker.IntentBuilder().build(activity);
                startActivityForResult(intent, REQUEST_CODE_PLACE_PICKER);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                onSnackbar(R.string.snackbar_failed);
                // TODO: renable the action to pick a place.
            }
        }

    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }

    public interface InteractionListener {

        void onUserAreaSelected(@NonNull String areaId);
    }
}