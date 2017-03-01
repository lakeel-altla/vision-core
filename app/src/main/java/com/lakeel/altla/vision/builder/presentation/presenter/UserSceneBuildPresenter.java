package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoConfig;

import com.lakeel.altla.rajawali.pool.Pool;
import com.lakeel.altla.rajawali.pool.QuaternionPool;
import com.lakeel.altla.rajawali.pool.Vector3Pool;
import com.lakeel.altla.tango.OnFrameAvailableListener;
import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.module.Names;
import com.lakeel.altla.vision.builder.presentation.model.ActorEditMode;
import com.lakeel.altla.vision.builder.presentation.model.Axis;
import com.lakeel.altla.vision.builder.presentation.model.EditAxesModel;
import com.lakeel.altla.vision.builder.presentation.model.SceneBuildModel;
import com.lakeel.altla.vision.builder.presentation.model.UserActorImageModel;
import com.lakeel.altla.vision.builder.presentation.model.UserActorModel;
import com.lakeel.altla.vision.builder.presentation.model.UserAssetImageDragModel;
import com.lakeel.altla.vision.builder.presentation.view.UserSceneBuildView;
import com.lakeel.altla.vision.builder.presentation.view.renderer.MainRenderer;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.model.UserActor;
import com.lakeel.altla.vision.domain.usecase.GetUserAssetImageFileUriUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveAllUserActorsUserCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserActorUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Defines the presenter for {@link UserSceneBuildView}.
 */
public final class UserSceneBuildPresenter extends BasePresenter<UserSceneBuildView>
        implements OnFrameAvailableListener, MainRenderer.OnCurrentCameraTransformUpdatedListener,
                   MainRenderer.OnUserActorPickedListener {

    private static final String ARG_AREA_ID = "areaId";

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    private static final String ARG_SCENE_ID = "sceneId";

    private static final float ACTOR_DROP_POSITION_ADJUSTMENT = 2f;

    private static final float TRANSLATE_OBJECT_DISTANCE_SCALE = 0.005f;

    private static final float ROTATE_OBJECT_ANGLE_SCALE = 1f;

    private static final float SCALE_OBJECT_SIZE_SCALE = 0.5f;

    @Named(Names.ACTIVITY_CONTEXT)
    @Inject
    Context context;

    @Inject
    TangoWrapper tangoWrapper;

    @Inject
    ObserveAllUserActorsUserCase observeAllUserActorsUserCase;

    @Inject
    GetUserAssetImageFileUriUseCase getUserAssetImageFileUriUseCase;

    @Inject
    SaveUserActorUseCase saveUserActorUseCase;

    private final UserActorManager userActorManager = new UserActorManager();

    private final Vector3 cameraPosition = new Vector3();

    private final Quaternion cameraOrientation = new Quaternion();

    private final Vector3 cameraForward = new Vector3();

    private String areaId;

    private String areaDescriptionId;

    private String sceneId;

    private MainRenderer renderer;

    private UserActorModel pickedUserActorModel;

    private volatile boolean active = true;

    private ActorEditMode actorEditMode = ActorEditMode.NONE;

    private Axis translateAxis = Axis.X;

    private Axis rotateAxis = Axis.Y;

    private boolean debugConsoleVisible;

    @Inject
    public UserSceneBuildPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull SceneBuildModel sceneBuildModel) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AREA_ID, sceneBuildModel.areaId);
        bundle.putString(ARG_AREA_DESCRIPTION_ID, sceneBuildModel.areaDescriptionId);
        bundle.putString(ARG_SCENE_ID, sceneBuildModel.sceneId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        String areaId = arguments.getString(ARG_AREA_ID);
        if (areaId == null) {
            throw new IllegalStateException(String.format("Argument '%s' must be not null.", ARG_AREA_ID));
        }
        this.areaId = areaId;

        String areaDescriptionId = arguments.getString(ARG_AREA_DESCRIPTION_ID);
        if (areaDescriptionId == null) {
            throw new IllegalStateException(String.format("Argument '%s' must be not null.", ARG_AREA_DESCRIPTION_ID));
        }
        this.areaDescriptionId = areaDescriptionId;

        String sceneId = arguments.getString(ARG_SCENE_ID);
        if (sceneId == null) {
            throw new IllegalStateException(String.format("Argument '%s' must be not null.", ARG_SCENE_ID));
        }
        this.sceneId = sceneId;

        tangoWrapper.setTangoConfigFactory(this::createTangoConfig);
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().setTangoUxLayout(tangoWrapper.getTangoUx());

        renderer = new MainRenderer(context);
        renderer.setOnCurrentCameraTransformUpdatedListener(this);
        renderer.setOnUserActorPickedListener(this);
        getView().setSurfaceRenderer(renderer);

        getView().onUpdateObjectMenuVisible(false);
        getView().onUpdateTranslateSelected(false);
        getView().onUpdateRotateSelected(false);
        getView().onUpdateTranslateMenuVisible(false);
        getView().onUpdateRotateMenuVisible(false);
        getView().onUpdateTranslateAxisSelected(Axis.X, true);
        getView().onUpdateRotateAxisSelected(Axis.Y, true);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        Disposable disposable = observeAllUserActorsUserCase
                .execute(sceneId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    userActorManager.handle(event);
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable);
    }

    @Override
    protected void onResumeOverride() {
        super.onResumeOverride();

        tangoWrapper.addOnTangoReadyListener(renderer::connectToTangoCamera);
        tangoWrapper.addOnFrameAvailableListener(this);
        tangoWrapper.connect();
        active = true;
    }

    @Override
    protected void onPauseOverride() {
        super.onPauseOverride();

        active = false;
        tangoWrapper.removeOnTangoReadyListener(renderer::connectToTangoCamera);
        tangoWrapper.removeOnFrameAvailableListener(this);
        tangoWrapper.disconnect();
        renderer.disconnectFromTangoCamera();
    }

    @Override
    public void onFrameAvailable(int cameraId) {
        if (active && cameraId == TangoCameraIntrinsics.TANGO_CAMERA_COLOR) {
            renderer.onFrameAvailable();
        }
    }

    @Override
    public void onCurrentCameraTransformUpdated(double positionX, double positionY, double positionZ,
                                                double orientationX, double orientationY, double orientationZ,
                                                double orientationW, double forwardX, double forwardY,
                                                double forwardZ) {
        cameraPosition.setAll(positionX, positionY, positionZ);
        cameraOrientation.setAll(orientationW, orientationX, orientationY, orientationZ);
        cameraForward.setAll(forwardX, forwardY, forwardZ);
    }

    @Override
    public void onUserActorPicked(@Nullable UserActorModel userActorModel) {
        pickedUserActorModel = userActorModel;
        getView().onUpdateObjectMenuVisible(pickedUserActorModel != null);
    }

    public void onTouchButtonTranslate() {
        actorEditMode = ActorEditMode.TRANSLATE;

        getView().onUpdateTranslateSelected(true);
        getView().onUpdateTranslateMenuVisible(true);
        getView().onUpdateRotateSelected(false);
        getView().onUpdateRotateMenuVisible(false);
        getView().onUpdateScaleSelected(false);
    }

    public void onTouchButtonRotateObject() {
        actorEditMode = ActorEditMode.ROTATE;

        getView().onUpdateTranslateSelected(false);
        getView().onUpdateTranslateMenuVisible(false);
        getView().onUpdateRotateSelected(true);
        getView().onUpdateRotateMenuVisible(true);
        getView().onUpdateScaleSelected(false);
    }

    public void onTouchButtonTranslateAxis(Axis axis) {
        translateAxis = axis;

        getView().onUpdateTranslateAxisSelected(Axis.X, axis == Axis.X);
        getView().onUpdateTranslateAxisSelected(Axis.Y, axis == Axis.Y);
        getView().onUpdateTranslateAxisSelected(Axis.Z, axis == Axis.Z);
    }

    public void onTouchButtonRotateAxis(Axis axis) {
        rotateAxis = axis;

        getView().onUpdateRotateAxisSelected(Axis.X, axis == Axis.X);
        getView().onUpdateRotateAxisSelected(Axis.Y, axis == Axis.Y);
        getView().onUpdateRotateAxisSelected(Axis.Z, axis == Axis.Z);
    }

    public void onTouchButtonScale() {
        actorEditMode = ActorEditMode.SCALE;

        getView().onUpdateTranslateSelected(false);
        getView().onUpdateTranslateMenuVisible(false);
        getView().onUpdateRotateSelected(false);
        getView().onUpdateRotateMenuVisible(false);
        getView().onUpdateScaleSelected(true);
    }

    public void onDropModel(@NonNull ClipData clipData) {
        if (clipData.getItemCount() == 0) throw new IllegalStateException("No item.");

        Intent intent = clipData.getItemAt(0).getIntent();
        if (intent == null) throw new IllegalStateException("No intent.");

        UserAssetImageDragModel userAssetImageDragModel = UserAssetImageDragModel.parseIntent(intent);
        if (userAssetImageDragModel == null) throw new IllegalStateException("No UserAssetImageDragModel.");

        UserActor userActor = new UserActor(userAssetImageDragModel.userId,
                                            sceneId,
                                            UUID.randomUUID().toString());
        userActor.assetType = UserActor.AssetType.IMAGE;
        userActor.assetId = userAssetImageDragModel.assetId;

        // Decide the position and the orientation of the dropped user actor.
        try (Pool.Holder<Vector3> positionHolder = Vector3Pool.get();
             Pool.Holder<Quaternion> orientationHolder = QuaternionPool.get();
             Pool.Holder<Vector3> translationHolder = Vector3Pool.get();
             Pool.Holder<Vector3> cameraBackwardHolder = Vector3Pool.get()) {

            Vector3 position = positionHolder.get();
            Quaternion orientation = orientationHolder.get();
            Vector3 translation = translationHolder.get();
            Vector3 cameraBackward = cameraBackwardHolder.get();

            position.setAll(cameraPosition);

            translation.setAll(cameraForward);
            translation.multiply(ACTOR_DROP_POSITION_ADJUSTMENT);

            position.add(translation);

            cameraBackward.setAll(cameraForward);
            cameraBackward.inverse();

            orientation.lookAt(cameraBackward, Vector3.Y);

            userActor.positionX = position.x;
            userActor.positionY = position.y;
            userActor.positionZ = position.z;
            userActor.orientationX = orientation.x;
            userActor.orientationY = orientation.y;
            userActor.orientationZ = orientation.z;
            userActor.orientationW = orientation.w;
        }

        Disposable disposable = saveUserActorUseCase
                .execute(userActor)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public boolean onSingleTapUp(MotionEvent e) {
        renderer.tryPickObject(e.getX(), e.getY());
        return true;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (pickedUserActorModel != null) {
            if (actorEditMode == ActorEditMode.TRANSLATE) {
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    translateUserActor(distanceX);
                } else {
                    translateUserActor(distanceY);
                }
            } else if (actorEditMode == ActorEditMode.ROTATE) {
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    rotateUserActor(distanceX);
                } else {
                    rotateUserActor(distanceY);
                }
            } else if (actorEditMode == ActorEditMode.SCALE) {
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    scaleUserActor(distanceX);
                } else {
                    scaleUserActor(distanceY);
                }
            }

            return true;
        }
        return false;
    }

    public void onScrollFinished(MotionEvent event) {
        if (pickedUserActorModel == null) return;

        UserActor userActor;
        if (pickedUserActorModel instanceof UserActorImageModel) {
            userActor = map((UserActorImageModel) pickedUserActorModel);
        } else {
            throw new IllegalStateException(
                    "Unknown UserActorModel sub-class: " + pickedUserActorModel.getClass().getName());
        }

        // Save.
        Disposable disposable = saveUserActorUseCase
                .execute(userActor)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable);
    }

    private void translateUserActor(float distance) {
        // Scale.
        float scaledDistance = distance * TRANSLATE_OBJECT_DISTANCE_SCALE;

        // Translate.
        try (Pool.Holder<Vector3> translationHolder = Vector3Pool.get();
             Pool.Holder<Quaternion> rotationHolder = QuaternionPool.get();
             Pool.Holder<Vector3> positionHolder = Vector3Pool.get()) {

            Vector3 translation = translationHolder.get();
            Quaternion rotation = rotationHolder.get();
            Vector3 position = positionHolder.get();

            switch (translateAxis) {
                case X:
                    translation.setAll(Vector3.X);
                    break;
                case Y:
                    translation.setAll(Vector3.Y);
                    break;
                case Z:
                default:
                    translation.setAll(Vector3.Z);
                    break;
            }

            rotation.setAll(pickedUserActorModel.orientation);
            // Conjugate because rotateBy is wrong...
            rotation.conjugate();

            translation.rotateBy(rotation);
            translation.normalize();
            translation.multiply(scaledDistance);

            position.setAll(pickedUserActorModel.position);
            position.add(translation);

            pickedUserActorModel.position.setAll(position);
        }

        EditAxesModel editAxesModel = new EditAxesModel();
        editAxesModel.position.setAll(pickedUserActorModel.position);
        editAxesModel.orientation.setAll(pickedUserActorModel.orientation);

        renderer.updateUserActorModel(pickedUserActorModel);
        renderer.updateEditAxesModel(editAxesModel);
    }

    private void rotateUserActor(float angle) {
        // Scale.
        float scaledAngle = angle * ROTATE_OBJECT_ANGLE_SCALE;

        // Rotate.
        try (Pool.Holder<Vector3> baseAxisHolder = Vector3Pool.get();
             Pool.Holder<Vector3> modelAxisHolder = Vector3Pool.get();
             Pool.Holder<Quaternion> rotationHolder = QuaternionPool.get();
             Pool.Holder<Quaternion> axisRotationHolder = QuaternionPool.get()) {

            Vector3 baseAxis = baseAxisHolder.get();
            Vector3 modelAxis = modelAxisHolder.get();
            Quaternion rotation = rotationHolder.get();
            Quaternion axisRotation = axisRotationHolder.get();

            switch (rotateAxis) {
                case X:
                    baseAxis.setAll(Vector3.X);
                    break;
                case Y:
                    baseAxis.setAll(Vector3.Y);
                    break;
                case Z:
                default:
                    baseAxis.setAll(Vector3.Z);
                    break;
            }

            modelAxis.setAll(baseAxis);
            rotation.setAll(pickedUserActorModel.orientation);
            // Conjugate because rotateBy is wrong...
            rotation.conjugate();
            modelAxis.rotateBy(rotation);

            axisRotation.fromAngleAxis(modelAxis, scaledAngle);
            pickedUserActorModel.orientation.multiply(axisRotation);
        }

        EditAxesModel editAxesModel = new EditAxesModel();
        editAxesModel.position.setAll(pickedUserActorModel.position);
        editAxesModel.orientation.setAll(pickedUserActorModel.orientation);

        renderer.updateUserActorModel(pickedUserActorModel);
        renderer.updateEditAxesModel(editAxesModel);
    }

    private void scaleUserActor(float size) {

        // Scale the raw value.
        float scaledSize = size * SCALE_OBJECT_SIZE_SCALE;

        final float MIN_RATIO = 0.1f;

        try (Pool.Holder<Vector3> scaleHolder = Vector3Pool.get()) {
            Vector3 scale = scaleHolder.get();

            float ratio;
            if (0 < scaledSize) {
                ratio = 1 + scaledSize * 0.01f;
            } else {
                ratio = Math.max(1 - Math.abs(scaledSize) * 0.01f, MIN_RATIO);
            }

            scale.setAll(pickedUserActorModel.scale);
            scale.multiply(ratio);

            pickedUserActorModel.scale.setAll(scale);
        }

        renderer.updateUserActorModel(pickedUserActorModel);
    }

    @NonNull
    private TangoConfig createTangoConfig(@NonNull Tango tango) {
        TangoConfig config = tango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);

        // NOTE:
        // Low latency integration is necessary to achieve a precise alignment of
        // virtual objects with the RBG image and produce a good AR effect.
        config.putBoolean(TangoConfig.KEY_BOOLEAN_LOWLATENCYIMUINTEGRATION, true);
        // Enable the color camera.
        config.putBoolean(TangoConfig.KEY_BOOLEAN_COLORCAMERA, true);
        // NOTE:
        // Javadoc says, "LEARNINGMODE and loading AREADESCRIPTION cannot be used if drift correction is enabled."
//        config.putBoolean(TangoConfig.KEY_BOOLEAN_DRIFT_CORRECTION, true);

        if (areaDescriptionId != null) {
            config.putString(TangoConfig.KEY_STRING_AREADESCRIPTION, areaDescriptionId);
        }

        return config;
    }

    public void onToggleDebug() {
        debugConsoleVisible = !debugConsoleVisible;
        getView().onUpdateDebugConsoleVisible(debugConsoleVisible);
    }

    @NonNull
    private static UserActorImageModel map(@NonNull UserActor userActor) {
        UserActorImageModel model = new UserActorImageModel(userActor.userId,
                                                            userActor.sceneId,
                                                            userActor.actorId,
                                                            userActor.assetId);
        model.position.setAll(userActor.positionX, userActor.positionY, userActor.positionZ);
        model.orientation.setAll(userActor.orientationW,
                                 userActor.orientationX,
                                 userActor.orientationY,
                                 userActor.orientationZ);
        model.scale.setAll(userActor.scaleX, userActor.scaleY, userActor.scaleZ);
        model.createdAt = userActor.createdAt;
        model.updatedAt = userActor.updatedAt;
        return model;
    }

    @NonNull
    private static UserActor map(@NonNull UserActorImageModel userActorImageModel) {
        UserActor userActor = new UserActor(userActorImageModel.userId,
                                            userActorImageModel.sceneId,
                                            userActorImageModel.actorId);
        userActor.assetType = UserActor.AssetType.IMAGE;
        userActor.assetId = userActorImageModel.assetId;
        userActor.positionX = userActorImageModel.position.x;
        userActor.positionY = userActorImageModel.position.y;
        userActor.positionZ = userActorImageModel.position.z;
        userActor.orientationX = userActorImageModel.orientation.x;
        userActor.orientationY = userActorImageModel.orientation.y;
        userActor.orientationZ = userActorImageModel.orientation.z;
        userActor.orientationW = userActorImageModel.orientation.w;
        userActor.scaleX = userActorImageModel.scale.x;
        userActor.scaleY = userActorImageModel.scale.y;
        userActor.scaleZ = userActorImageModel.scale.z;
        userActor.createdAt = userActorImageModel.createdAt;
        userActor.updatedAt = userActorImageModel.updatedAt;
        return userActor;
    }

    private final class UserActorManager {

        final Set<Target> targetMap = new HashSet<>();

        void handle(@NonNull DataListEvent<UserActor> event) {
            getLog().v("DataListEvent<UserActor>: type = %s, actorId = %s", event.getType(),
                       event.getData().actorId);

            switch (event.getType()) {
                case ADDED:
                    onAdded(event.getData());
                    break;
                case CHANGED:
                    onChanged(event.getData());
                    break;
                case MOVED:
                    // Ignore.
                    break;
                case REMOVED:
                    onRemoved(event.getData());
                    break;
            }
        }

        void onAdded(@NonNull UserActor userActor) {
            switch (userActor.assetType) {
                case IMAGE:
                    onUserActorImageAdded(userActor);
                    break;
                default:
                    throw new IllegalStateException("Unknown asset type: " + userActor.assetType);
            }
        }

        void onChanged(@NonNull UserActor userActor) {
            switch (userActor.assetType) {
                case IMAGE:
                    onUserActorImageChanged(userActor);
                    break;
                default:
                    throw new IllegalStateException("Unknown asset type: " + userActor.assetType);
            }
        }

        void onRemoved(@NonNull UserActor userActor) {
            // TODO
        }

        void onUserActorImageAdded(@NonNull UserActor userActor) {
            Disposable disposable = getUserAssetImageFileUriUseCase
                    .execute(userActor.assetId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(uri -> {
                        final UserActorImageModel model = map(userActor);

                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                getLog().d("onBitmapLoaded: actorId = %s, uri = %s", model.actorId, uri);

                                model.bitmap = bitmap;

                                renderer.addUserActorModel(model);

                                // Dereference.
                                targetMap.remove(this);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                getLog().e("onBitmapFailed: actorId = %s, uri = %s", model.actorId, uri);

                                // TODO

                                // Dereference.
                                targetMap.remove(this);
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                            }
                        };
                        // Hold the strong reference to the Target instance,
                        // because Piccaso uses it as a weak reference.
                        targetMap.add(target);

                        Picasso.with(context)
                               .load(uri)
                               .into(target);
                    }, e -> {
                        getLog().e("Failed.", e);
                    });
            manageDisposable(disposable);
        }

        void onUserActorImageChanged(@NonNull UserActor userActor) {
            UserActorImageModel model = map(userActor);
            renderer.updateUserActorModel(model);
        }
    }
}
