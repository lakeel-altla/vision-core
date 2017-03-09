package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.UserActorView;
import com.lakeel.altla.vision.domain.usecase.ObserveUserActorUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserActorPresenter extends BasePresenter<UserActorView> {

    private static final String ARG_SCENE_ID = "sceneId";

    private static final String ARG_ACTOR_ID = "actorId";

    @Inject
    ObserveUserActorUseCase observeUserActorUseCase;

    private String sceneId;

    private String actorId;

    @Inject
    public UserActorPresenter() {
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
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        getView().onUpdateTitle(null);

        Disposable disposable = observeUserActorUseCase
                .execute(sceneId, actorId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(actor -> {
                    getView().onUpdateTitle(actor.getName());
                    getView().onUpdateActorId(actor.getId());
                    getView().onUpdateName(actor.getName());
                    getView().onUpdatePosition(actor.getPositionX(), actor.getPositionY(), actor.getPositionZ());
                    getView().onUpdateOrientation(actor.getOrientationX(),
                                                  actor.getOrientationY(),
                                                  actor.getOrientationZ(),
                                                  actor.getOrientationW());
                    getView().onUpdateScale(actor.getScaleX(), actor.getScaleY(), actor.getScaleZ());
                    getView().onUpdateCreatedAt(actor.getCreatedAtAsLong());
                    getView().onUpdateUpdatedAt(actor.getUpdatedAtAsLong());
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onEdit() {
        getView().onShowUserActorEditView(sceneId, actorId);
    }
}
