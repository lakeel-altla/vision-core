package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.UserActorEditView;
import com.lakeel.altla.vision.domain.model.Actor;
import com.lakeel.altla.vision.domain.usecase.FindUserActorUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserActorUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserActorEditPresenter extends BasePresenter<UserActorEditView> {

    private static final String ARG_SCENE_ID = "sceneId";

    private static final String ARG_ACTOR_ID = "actorId";

    private static final String STATE_ACTOR = "actor";

    @Inject
    FindUserActorUseCase findUserActorUseCase;

    @Inject
    SaveUserActorUseCase saveUserActorUseCase;

    private String sceneId;

    private String actorId;

    private Actor actor;

    @Inject
    public UserActorEditPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull String sceneId, @NonNull String actorId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SCENE_ID, sceneId);
        bundle.putString(ARG_ACTOR_ID, actorId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        String sceneId = arguments.getString(ARG_SCENE_ID);
        if (sceneId == null) {
            throw new IllegalArgumentException(String.format("Argument '%s' must be not null.", ARG_SCENE_ID));
        }

        String actorId = arguments.getString(ARG_ACTOR_ID);
        if (actorId == null) {
            throw new IllegalArgumentException(String.format("Argument '%s' must be not null.", ARG_ACTOR_ID));
        }

        this.sceneId = sceneId;
        this.actorId = actorId;

        if (savedInstanceState == null) {
            actor = null;
        } else {
            actor = Parcels.unwrap(savedInstanceState.getParcelable(STATE_ACTOR));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_ACTOR, Parcels.wrap(actor));
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(actorId);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        getView().onUpdateHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        getView().onUpdateViewsEnabled(false);
        getView().onUpdateActionSave(false);

        if (actor == null) {
            Disposable disposable = findUserActorUseCase
                    .execute(sceneId, actorId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(actor -> {
                        this.actor = actor;
                        updateViews(actor);
                        getView().onUpdateViewsEnabled(true);
                        getView().onUpdateActionSave(canSave());
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    });
            manageDisposable(disposable);
        } else {
            updateViews(actor);
            getView().onUpdateViewsEnabled(true);
            getView().onUpdateActionSave(canSave());
        }
    }

    @Override
    protected void onStopOverride() {
        super.onStopOverride();

        getView().onUpdateHomeAsUpIndicator(null);
    }

    public void onEditTextNameAfterTextChanged(String value) {
        actor.setName(value);
        getView().onHideNameError();

        // Don't save the empty name.
        if (actor.getName() == null || actor.getName().length() == 0) {
            getView().onShowNameError(R.string.input_error_required);
        }

        getView().onUpdateActionSave(canSave());
    }

    public void onEditTextPositionXAfterTextChanged(String value) {
        actor.setPositionX(Double.parseDouble(value));
    }

    public void onEditTextPositionYAfterTextChanged(String value) {
        actor.setPositionY(Double.parseDouble(value));
    }

    public void onEditTextPositionZAfterTextChanged(String value) {
        actor.setPositionZ(Double.parseDouble(value));
    }

    public void onEditTextOrientationXAfterTextChanged(String value) {
        actor.setOrientationX(Double.parseDouble(value));
    }

    public void onEditTextOrientationYAfterTextChanged(String value) {
        actor.setOrientationY(Double.parseDouble(value));
    }

    public void onEditTextOrientationZAfterTextChanged(String value) {
        actor.setOrientationZ(Double.parseDouble(value));
    }

    public void onEditTextOrientationWAfterTextChanged(String value) {
        actor.setOrientationW(Double.parseDouble(value));
    }

    public void onEditTextScaleXAfterTextChanged(String value) {
        actor.setScaleX(Double.parseDouble(value));

        getView().onUpdateActionSave(canSave());
    }

    public void onEditTextScaleYAfterTextChanged(String value) {
        actor.setScaleY(Double.parseDouble(value));

        getView().onUpdateActionSave(canSave());
    }

    public void onEditTextScaleZAfterTextChanged(String value) {
        actor.setScaleZ(Double.parseDouble(value));

        getView().onUpdateActionSave(canSave());
    }

    public void onActionSave() {
        getView().onUpdateViewsEnabled(false);
        getView().onUpdateActionSave(false);

        Disposable disposable = saveUserActorUseCase
                .execute(actor)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    getView().onSnackbar(R.string.snackbar_done);
                    getView().onBackView();
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    private boolean canSave() {
        return 0 < actor.getScaleX() &&
               0 < actor.getScaleY() &&
               0 < actor.getScaleZ() &&
               actor.getName() != null &&
               actor.getName().length() != 0;
    }

    private void updateViews(@NonNull Actor actor) {
        getView().onUpdateName(actor.getName());
        getView().onUpdatePositionX(actor.getPositionX());
        getView().onUpdatePositionY(actor.getPositionY());
        getView().onUpdatePositionZ(actor.getOrientationZ());
        getView().onUpdateOrientationX(actor.getOrientationX());
        getView().onUpdateOrientationY(actor.getOrientationY());
        getView().onUpdateOrientationZ(actor.getOrientationZ());
        getView().onUpdateOrientationW(actor.getOrientationW());
        getView().onUpdateScaleX(actor.getScaleX());
        getView().onUpdateScaleY(actor.getScaleY());
        getView().onUpdateScaleZ(actor.getScaleZ());
    }
}
