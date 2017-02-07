package com.lakeel.altla.vision.builder.presentation.view.fragment;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionModel;
import com.lakeel.altla.vision.builder.presentation.presenter.UserAreaDescriptionPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionView;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserAreaDescriptionFragment extends Fragment implements UserAreaDescriptionView {

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    @Inject
    UserAreaDescriptionPresenter presenter;

    @BindView(R.id.text_view_id)
    TextView textViewId;

    @BindView(R.id.text_view_name)
    TextView textViewName;

    private InteractionListener interactionListener;

    public static UserAreaDescriptionFragment newInstance(String areaDescriptionId) {
        UserAreaDescriptionFragment fragment = new UserAreaDescriptionFragment();
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
            throw new IllegalStateException(String.format("Argument '%s' must be not null", ARG_AREA_DESCRIPTION_ID));
        }

        presenter.onCreate(areaDescriptionId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_area_description, container, false);
        ButterKnife.bind(this, view);
        presenter.onCreateView(this);
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
    public void showModel(@NonNull UserAreaDescriptionModel model) {
        textViewId.setText(model.areaDescriptionId);
        textViewName.setText(model.name);
    }

    public interface InteractionListener {

    }
}
