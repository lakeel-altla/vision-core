package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.presenter.UserAreaDescriptionsPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionsView;
import com.lakeel.altla.vision.builder.presentation.view.adapter.UserAreaDescriptionModelAdapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

public final class UserAreaDescriptionsFragment extends Fragment implements UserAreaDescriptionsView {

    @Inject
    UserAreaDescriptionsPresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private InterationListener interationListener;

    public static UserAreaDescriptionsFragment newInstance() {
        return new UserAreaDescriptionsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
        interationListener = InterationListener.class.cast(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_area_description_list, container, false);
        ButterKnife.bind(this, view);

        presenter.onCreateView(this);

        recyclerView.setAdapter(new UserAreaDescriptionModelAdapter(presenter));
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
    public void updateItems() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void updateItem(int position) {
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void showSnackbar(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showUserAreaDescriptionScenes(@NonNull String areaDescriptionId) {
        interationListener.onShowUserAreaDescriptionScenesFragment(areaDescriptionId);
    }

    public interface InterationListener {

        void onShowUserAreaDescriptionScenesFragment(@NonNull String areaDescriptionId);
    }
}
