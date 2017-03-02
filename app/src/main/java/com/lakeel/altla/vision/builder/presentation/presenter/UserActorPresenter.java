package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.UserActorView;
import com.lakeel.altla.vision.domain.model.UserActor;
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
                .map(this::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    getView().onUpdateTitle(model.name);
                    getView().onUpdateActorId(model.actorId);
                    getView().onUpdateName(model.name);
                    getView().onUpdatePosition(model.positionX, model.positionY, model.positionZ);
                    getView().onUpdateOrientation(model.orientationX,
                                                  model.orientationY,
                                                  model.orientationZ,
                                                  model.orientationW);
                    getView().onUpdateScale(model.scaleX, model.scaleY, model.scaleZ);
                    getView().onUpdateCreatedAt(model.createdAt);
                    getView().onUpdateUpdatedAt(model.updatedAt);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onEdit() {
        getView().onShowUserActorEditView(sceneId, actorId);
    }

    @NonNull
    private Model map(@NonNull UserActor userActor) {
        Model model = new Model();
        model.actorId = userActor.actorId;
        model.name = userActor.name;
        model.positionX = userActor.positionX;
        model.positionY = userActor.positionY;
        model.positionZ = userActor.positionZ;
        model.orientationX = userActor.orientationX;
        model.orientationY = userActor.orientationY;
        model.orientationZ = userActor.orientationZ;
        model.orientationW = userActor.orientationW;
        model.scaleX = userActor.scaleX;
        model.scaleY = userActor.scaleY;
        model.scaleZ = userActor.scaleZ;
        model.createdAt = userActor.createdAt;
        model.updatedAt = userActor.updatedAt;
        return model;
    }

    private final class Model {

        String actorId;

        String name;

        double positionX;

        double positionY;

        double positionZ;

        double orientationX;

        double orientationY;

        double orientationZ;

        double orientationW;

        double scaleX = 1;

        double scaleY = 1;

        double scaleZ = 1;

        long createdAt;

        long updatedAt;
    }
}
