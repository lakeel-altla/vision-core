package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.UserActorEditView;
import com.lakeel.altla.vision.domain.model.UserActor;
import com.lakeel.altla.vision.domain.usecase.FindUserActorUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserActorUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import org.parceler.Parcel;
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

    private static final String STATE_MODEL = "model";

    @Inject
    FindUserActorUseCase findUserActorUseCase;

    @Inject
    SaveUserActorUseCase saveUserActorUseCase;

    private String sceneId;

    private String actorId;

    private Model model;

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
            model = null;
        } else {
            model = Parcels.unwrap(savedInstanceState.getParcelable(STATE_MODEL));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_MODEL, Parcels.wrap(model));
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

        if (model == null) {
            Disposable disposable = findUserActorUseCase
                    .execute(sceneId, actorId)
                    .map(UserActorEditPresenter::map)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(model -> {
                        this.model = model;
                        updateViews(model);
                        getView().onUpdateViewsEnabled(true);
                        getView().onUpdateActionSave(canSave());
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    });
            manageDisposable(disposable);
        } else {
            updateViews(model);
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
        model.name = value;
        getView().onHideNameError();

        // Don't save the empty name.
        if (model.name == null || model.name.length() == 0) {
            getView().onShowNameError(R.string.input_error_required);
        }

        getView().onUpdateActionSave(canSave());
    }

    public void onEditTextPositionXAfterTextChanged(String value) {
        model.positionX = Double.parseDouble(value);
    }

    public void onEditTextPositionYAfterTextChanged(String value) {
        model.positionY = Double.parseDouble(value);
    }

    public void onEditTextPositionZAfterTextChanged(String value) {
        model.positionZ = Double.parseDouble(value);
    }

    public void onEditTextOrientationXAfterTextChanged(String value) {
        model.orientationX = Double.parseDouble(value);
    }

    public void onEditTextOrientationYAfterTextChanged(String value) {
        model.orientationY = Double.parseDouble(value);
    }

    public void onEditTextOrientationZAfterTextChanged(String value) {
        model.orientationZ = Double.parseDouble(value);
    }

    public void onEditTextOrientationWAfterTextChanged(String value) {
        model.orientationW = Double.parseDouble(value);
    }

    public void onEditTextScaleXAfterTextChanged(String value) {
        model.scaleX = Double.parseDouble(value);

        getView().onUpdateActionSave(canSave());
    }

    public void onEditTextScaleYAfterTextChanged(String value) {
        model.scaleY = Double.parseDouble(value);

        getView().onUpdateActionSave(canSave());
    }

    public void onEditTextScaleZAfterTextChanged(String value) {
        model.scaleZ = Double.parseDouble(value);

        getView().onUpdateActionSave(canSave());
    }

    public void onActionSave() {
        getView().onUpdateViewsEnabled(false);
        getView().onUpdateActionSave(false);

        UserActor userActor = map(model);

        Disposable disposable = saveUserActorUseCase
                .execute(userActor)
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
        return 0 < model.scaleX &&
               0 < model.scaleY &&
               0 < model.scaleZ &&
               model.name != null &&
               model.name.length() != 0;
    }

    private void updateViews(@NonNull Model model) {
        getView().onUpdateName(model.name);
        getView().onUpdatePositionX(model.positionX);
        getView().onUpdatePositionY(model.positionY);
        getView().onUpdatePositionZ(model.positionZ);
        getView().onUpdateOrientationX(model.orientationX);
        getView().onUpdateOrientationY(model.orientationY);
        getView().onUpdateOrientationZ(model.orientationZ);
        getView().onUpdateOrientationW(model.orientationW);
        getView().onUpdateScaleX(model.scaleX);
        getView().onUpdateScaleY(model.scaleY);
        getView().onUpdateScaleZ(model.scaleZ);
    }

    @NonNull
    private static Model map(@NonNull UserActor userActor) {
        Model model = new Model();
        model.userId = userActor.userId;
        model.sceneId = userActor.sceneId;
        model.actorId = userActor.actorId;
        model.assetTypeValue = userActor.assetType.getValue();
        model.assetId = userActor.assetId;
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

    @NonNull
    private static UserActor map(@NonNull Model model) {
        UserActor userActor = new UserActor(model.userId, model.sceneId, model.actorId);
        userActor.assetType = UserActor.AssetType.toAssetType(model.assetTypeValue);
        userActor.assetId = model.assetId;
        userActor.name = model.name;
        userActor.positionX = model.positionX;
        userActor.positionY = model.positionY;
        userActor.positionZ = model.positionZ;
        userActor.orientationX = model.orientationX;
        userActor.orientationY = model.orientationY;
        userActor.orientationZ = model.orientationZ;
        userActor.orientationW = model.orientationW;
        userActor.scaleX = model.scaleX;
        userActor.scaleY = model.scaleY;
        userActor.scaleZ = model.scaleZ;
        userActor.createdAt = model.createdAt;
        userActor.updatedAt = model.updatedAt;
        return userActor;
    }

    @Parcel
    public static final class Model {

        String userId;

        String sceneId;

        String actorId;

        int assetTypeValue;

        String assetId;

        String name;

        double positionX;

        double positionY;

        double positionZ;

        double orientationX;

        double orientationY;

        double orientationZ;

        double orientationW;

        double scaleX;

        double scaleY;

        double scaleZ;

        long createdAt;

        long updatedAt;
    }
}
