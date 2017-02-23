package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoPoseData;

import com.lakeel.altla.tango.OnFrameAvailableListener;
import com.lakeel.altla.tango.OnPoseAvailableListener;
import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.presentation.di.module.Names;
import com.lakeel.altla.vision.builder.presentation.model.Axis;
import com.lakeel.altla.vision.builder.presentation.model.MainDebugModel;
import com.lakeel.altla.vision.builder.presentation.model.ObjectEditMode;
import com.lakeel.altla.vision.builder.presentation.model.SceneBuildModel;
import com.lakeel.altla.vision.builder.presentation.model.UserActorImageModel;
import com.lakeel.altla.vision.builder.presentation.view.UserSceneBuildView;
import com.lakeel.altla.vision.builder.presentation.view.renderer.MainRenderer;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Defines the presenter for {@link UserSceneBuildView}.
 */
public final class UserSceneBuildPresenter extends BasePresenter<UserSceneBuildView>
        implements OnPoseAvailableListener, OnFrameAvailableListener, MainRenderer.OnPickedObjectChangedListener {

    private static final int SECS_TO_MILLISECS = 1000;

    private static final double UPDATE_DEBUG_CONSOLE_INTERVAL = 100;

    private static final String ARG_AREA_ID = "areaId";

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    private static final String ARG_SCENE_ID = "sceneId";

    @Named(Names.ACTIVITY_CONTEXT)
    @Inject
    Context context;

    @Inject
    TangoWrapper tangoWrapper;

    private final Handler handlerMain = new Handler(Looper.getMainLooper());

    private String areaId;

    private String areaDescriptionId;

    private String sceneId;

    private MainRenderer renderer;

    private boolean isModelPaneVisible;

    private boolean hasPickedObject;

    private volatile boolean active = true;

    private ObjectEditMode objectEditMode = ObjectEditMode.NONE;

    // on non-UI thread.
    private LocalizationState localizationState = LocalizationState.UNKNOWN;

    private MainDebugModel debugModel = new MainDebugModel();

    private PoseDebugger debuggerAd2Ss = new PoseDebugger(debugger -> {
        // on UI-thread.
        debugModel.ad2SsTranslation.x = debugger.translation.x;
        debugModel.ad2SsTranslation.y = debugger.translation.y;
        debugModel.ad2SsTranslation.z = debugger.translation.z;
        getView().onDebugModelUpdated(debugModel);
    });

    private PoseDebugger debuggerAd2D = new PoseDebugger(debugger -> {
        // on UI-thread.
        debugModel.ad2DTranslation.x = debugger.translation.x;
        debugModel.ad2DTranslation.y = debugger.translation.y;
        debugModel.ad2DTranslation.z = debugger.translation.z;
        getView().onDebugModelUpdated(debugModel);
    });

    private PoseDebugger debuggerSs2D = new PoseDebugger(debugger -> {
        // on UI-thread.
        debugModel.ss2DTranslation.x = debugger.translation.x;
        debugModel.ss2DTranslation.y = debugger.translation.y;
        debugModel.ss2DTranslation.z = debugger.translation.z;
        getView().onDebugModelUpdated(debugModel);
    });

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
        renderer.setOnPickedObjectChangedListener(this);
        getView().setSurfaceRenderer(renderer);

        getView().onUpdateObjectMenuVisible(false);
        getView().onUpdateTranslateObjectSelected(false);
        getView().onUpdateRotateObjectSelected(false);
        getView().onUpdateTranslateObjectMenuVisible(false);
        getView().onUpdateRotateObjectMenuVisible(false);
        getView().onUpdateTranslateObjectAxisSelected(Axis.X, true);
        getView().onUpdateRotateObjectAxisSelected(Axis.Y, true);

        getView().onDebugModelUpdated(debugModel);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();
    }

    @Override
    protected void onResumeOverride() {
        super.onResumeOverride();

        tangoWrapper.addOnTangoReadyListener(renderer::connectToTangoCamera);
        tangoWrapper.addOnPoseAvailableListener(this);
        tangoWrapper.addOnFrameAvailableListener(this);
        tangoWrapper.connect();
        active = true;
    }

    @Override
    protected void onPauseOverride() {
        super.onPauseOverride();

        active = false;
        tangoWrapper.removeOnTangoReadyListener(renderer::connectToTangoCamera);
        tangoWrapper.removeOnPoseAvailableListener(this);
        tangoWrapper.removeOnFrameAvailableListener(this);
        tangoWrapper.disconnect();
        renderer.disconnectFromTangoCamera();
    }

    @Override
    public void onPoseAvailable(TangoPoseData pose) {
        // NOTE: Invoked by Tango's thread that is not UI one.

        if (pose.baseFrame == TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION &&
            pose.targetFrame == TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE) {

            debuggerAd2Ss.debug(pose);

            if (pose.statusCode == TangoPoseData.POSE_VALID) {
                if (localizationState == LocalizationState.UNKNOWN ||
                    localizationState == LocalizationState.NOT_LOCALIZED) {
                    getLog().d("Localized.");
                    localizationState = LocalizationState.LOCALIZED;

                    handlerMain.post(() -> {
                        debugModel.localized = true;
                        getView().onDebugModelUpdated(debugModel);
                    });
                }
            } else {
                if (localizationState == LocalizationState.UNKNOWN ||
                    localizationState == LocalizationState.LOCALIZED) {
                    getLog().d("Not localized.");
                    localizationState = LocalizationState.NOT_LOCALIZED;

                    handlerMain.post(() -> {
                        debugModel.localized = false;
                        getView().onDebugModelUpdated(debugModel);
                    });
                }
            }
        }

        if (pose.baseFrame == TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION &&
            pose.targetFrame == TangoPoseData.COORDINATE_FRAME_DEVICE) {
            debuggerAd2D.debug(pose);
        }

        if (pose.baseFrame == TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE &&
            pose.targetFrame == TangoPoseData.COORDINATE_FRAME_DEVICE) {
            debuggerSs2D.debug(pose);
        }
    }

    @Override
    public void onFrameAvailable(int cameraId) {
        if (active && cameraId == TangoCameraIntrinsics.TANGO_CAMERA_COLOR) {
            renderer.onFrameAvailable();
        }
    }

    @Override
    public void onPickedObjectChanged(String oldName, String newName) {
        hasPickedObject = (newName != null);
        getView().onUpdateObjectMenuVisible(hasPickedObject);
    }

    public void onTouchButtonTranslateObject() {
        objectEditMode = ObjectEditMode.TRANSLATE;
        renderer.setObjectEditMode(objectEditMode);

        getView().onUpdateTranslateObjectSelected(true);
        getView().onUpdateTranslateObjectMenuVisible(true);
        getView().onUpdateRotateObjectSelected(false);
        getView().onUpdateRotateObjectMenuVisible(false);
        getView().onUpdateScaleObjectSelected(false);
    }

    public void onTouchButtonRotateObject() {
        objectEditMode = ObjectEditMode.ROTATE;
        renderer.setObjectEditMode(objectEditMode);

        getView().onUpdateTranslateObjectSelected(false);
        getView().onUpdateTranslateObjectMenuVisible(false);
        getView().onUpdateRotateObjectSelected(true);
        getView().onUpdateRotateObjectMenuVisible(true);
        getView().onUpdateScaleObjectSelected(false);
    }

    public void onTouchButtonTranslateObjectAxis(Axis axis) {
        renderer.setTranslateObjectAxis(axis);

        getView().onUpdateTranslateObjectAxisSelected(Axis.X, axis == Axis.X);
        getView().onUpdateTranslateObjectAxisSelected(Axis.Y, axis == Axis.Y);
        getView().onUpdateTranslateObjectAxisSelected(Axis.Z, axis == Axis.Z);
    }

    public void onTouchButtonRotateObjectAxis(Axis axis) {
        renderer.setRotateObjectAxis(axis);

        getView().onUpdateRotateObjectAxisSelected(Axis.X, axis == Axis.X);
        getView().onUpdateRotateObjectAxisSelected(Axis.Y, axis == Axis.Y);
        getView().onUpdateRotateObjectAxisSelected(Axis.Z, axis == Axis.Z);
    }

    public void onTouchButtonScaleObject() {
        objectEditMode = ObjectEditMode.SCALE;
        renderer.setObjectEditMode(objectEditMode);

        getView().onUpdateTranslateObjectSelected(false);
        getView().onUpdateTranslateObjectMenuVisible(false);
        getView().onUpdateRotateObjectSelected(false);
        getView().onUpdateRotateObjectMenuVisible(false);
        getView().onUpdateScaleObjectSelected(true);
    }

    public void onDropModel() {
        getLog().v("onDropModel");
        // TODO
//        if (0 <= singleTextureSelection.selectedPosition) {
//            TextureItemModel model = textureItems.get(singleTextureSelection.selectedPosition);
//            renderer.addPlaneBitmap(model.bitmap);
//        }
    }

    public void onDropModel(@NonNull ClipData clipData) {
        if (clipData.getItemCount() == 0) throw new IllegalStateException("No item.");

        Intent intent = clipData.getItemAt(0).getIntent();
        if (intent == null) throw new IllegalStateException("No intent.");

        UserActorImageModel userActorImageModel = UserActorImageModel.parseIntent(intent);
        if (userActorImageModel == null) throw new IllegalStateException("No UserActorImageModel.");

        renderer.addUserActorImage(userActorImageModel);
    }

    public boolean onSingleTapUp(MotionEvent e) {
        renderer.tryPickObject(e.getX(), e.getY());
        return true;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (hasPickedObject) {
            if (objectEditMode == ObjectEditMode.TRANSLATE) {
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    renderer.setTranslateObjectDistance(distanceX);
                } else {
                    renderer.setTranslateObjectDistance(distanceY);
                }
            } else if (objectEditMode == ObjectEditMode.ROTATE) {
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    renderer.setRotateObjectAngle(distanceX);
                } else {
                    renderer.setRotateObjectAngle(distanceY);
                }
            } else if (objectEditMode == ObjectEditMode.SCALE) {
                if (Math.abs(distanceY) < Math.abs(distanceX)) {
                    renderer.setScaleObjectSize(distanceX);
                } else {
                    renderer.setScaleObjectSize(distanceY);
                }
            }

            return true;
        }
        return false;
    }

    private TangoConfig createTangoConfig(Tango tango) {
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

    private enum LocalizationState {
        UNKNOWN,
        LOCALIZED,
        NOT_LOCALIZED
    }

    private class PoseDebugger {

        PoseDebugPrinter printer;

        double prevTimestamp;

        double timeToUpdate;

        final Translation translation = new Translation();

        PoseDebugger(PoseDebugPrinter printer) {
            this.printer = printer;
        }

        void debug(TangoPoseData pose) {
            double timeDelta = (pose.timestamp - prevTimestamp) * SECS_TO_MILLISECS;
            prevTimestamp = pose.timestamp;

            timeToUpdate -= timeDelta;

            if (timeToUpdate < 0) {
                timeToUpdate = UPDATE_DEBUG_CONSOLE_INTERVAL;
                translation.x = pose.translation[0];
                translation.y = pose.translation[1];
                translation.z = pose.translation[2];
                handlerMain.post(() -> printer.print(this));
            }
        }
    }

    private class Translation {

        double x;

        double y;

        double z;
    }

    private interface PoseDebugPrinter {

        void print(PoseDebugger debugger);
    }
}
