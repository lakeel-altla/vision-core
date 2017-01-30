package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.UserAreaDescriptionScenesPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionScenesView;
import com.lakeel.altla.vision.builder.presentation.view.adapter.UserAreaDescriptionSceneModelAdapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

public final class UserAreaDescriptionScenesFragment extends Fragment implements UserAreaDescriptionScenesView {

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    @Inject
    UserAreaDescriptionScenesPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private InteractionListener interactionListener;

    @NonNull
    public static UserAreaDescriptionScenesFragment newInstance(@NonNull String areaDescriptionId) {
        UserAreaDescriptionScenesFragment fragment = new UserAreaDescriptionScenesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AREA_DESCRIPTION_ID, areaDescriptionId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
        interactionListener = InteractionListener.class.cast(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null) throw new IllegalStateException("Arguments must be not null.");

        String areaDescriptionId = getArguments().getString(ARG_AREA_DESCRIPTION_ID);
        if (areaDescriptionId == null) {
            throw new IllegalStateException(String.format("Argument '%s' must be not null.", ARG_AREA_DESCRIPTION_ID));
        }

        presenter.onCreate(areaDescriptionId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_scene_list, container, false);
        ButterKnife.bind(this, view);

        presenter.onCreateView(this);

        recyclerView.setAdapter(new UserAreaDescriptionSceneModelAdapter(presenter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_area_description_scenes, menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_scene:
                // TODO
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateItems() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void updateItem(int position) {
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    public interface InteractionListener {

    }
}
