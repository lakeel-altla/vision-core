package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoPoseData;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.OnFrameAvailableListener;
import com.lakeel.altla.tango.OnPoseAvailableListener;
import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.module.Names;
import com.lakeel.altla.vision.builder.presentation.model.Axis;
import com.lakeel.altla.vision.builder.presentation.model.MainDebugModel;
import com.lakeel.altla.vision.builder.presentation.model.ObjectEditMode;
import com.lakeel.altla.vision.builder.presentation.model.TextureModel;
import com.lakeel.altla.vision.builder.presentation.view.MainView;
import com.lakeel.altla.vision.builder.presentation.view.TextureModelListItemView;
import com.lakeel.altla.vision.builder.presentation.view.renderer.MainRenderer;
import com.lakeel.altla.vision.domain.usecase.DeleteUserTextureUseCase;
import com.lakeel.altla.vision.domain.usecase.FindAllUserTexturesUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserTextureBitmapUseCase;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Defines the presenter for {@link MainView}.
 */
public final class MainPresenter
        implements OnPoseAvailableListener, OnFrameAvailableListener, MainRenderer.OnPickedObjectChangedListener {

    private static final Log LOG = LogFactory.getLog(MainPresenter.class);

    private static final int SECS_TO_MILLISECS = 1000;

    private static final double UPDATE_DEBUG_CONSOLE_INTERVAL = 100;

    @Named(Names.ACTIVITY_CONTEXT)
    @Inject
    Context context;

    @Inject
    FindAllUserTexturesUseCase findAllUserTexturesUseCase;

    @Inject
    FindUserTextureBitmapUseCase findUserTextureBitmapUseCase;

    @Inject
    DeleteUserTextureUseCase deleteUserTextureUseCase;

    @Inject
    TangoWrapper tangoWrapper;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final List<TextureModel> models = new ArrayList<>();

    private final SingleSelection selection = new SingleSelection();

    private final Handler handlerMain = new Handler(Looper.getMainLooper());

    private MainView view;

    private MainRenderer renderer;

    private boolean isModelPaneVisible;

    private boolean hasPickedObject;

    private volatile boolean active = true;

    private ObjectEditMode objectEditMode = ObjectEditMode.NONE;

    // on non-UI thread.
    private LocalizationState localizationState = LocalizationState.UNKNOWN;

    // on non-UI thread.
    private double prevAd2SsPoseTimestamp;

    // on non-UI thread.
    private double timeToNextUpdateAd2SsPose;

    // on non-UI thread.
    private double prevAd2DPoseTimestamp;

    // on non-UI thread.
    private double timeToNextUpdateAd2DPose;

    private MainDebugModel debugModel = new MainDebugModel();

    @Inject
    public MainPresenter() {
    }

    public void onCreateView(@NonNull MainView view) {
        LOG.v("onCreateView");

        this.view = view;

        this.view.setTangoUxLayout(tangoWrapper.getTangoUx());

        renderer = new MainRenderer(context);
        renderer.setOnPickedObjectChangedListener(this);
        this.view.setSurfaceRenderer(renderer);

        this.view.setTextureModelPaneVisible(false);
        this.view.setObjectMenuVisible(false);
        this.view.setTranslateObjectSelected(false);
        this.view.setRotateObjectSelected(false);
        this.view.setTranslateObjectMenuVisible(false);
        this.view.setRotateObjectMenuVisible(false);
        this.view.setTranslateObjectAxisSelected(Axis.X, true);
        this.view.setRotateObjectAxisSelected(Axis.Y, true);

        this.view.updateDebugModel(debugModel);
    }

    public void onStart() {
        models.clear();

        Disposable disposable = findAllUserTexturesUseCase
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userTexture -> {
                    TextureModel model = new TextureModel(userTexture.textureId, userTexture.name);
                    models.add(model);
                    view.updateTextureModelPane();
                }, e -> {
                    LOG.e("Failed to find all user textures.", e);
                });
        compositeDisposable.add(disposable);
    }

    public void onResume() {
        tangoWrapper.addOnTangoReadyListener(renderer::connectToTangoCamera);
        tangoWrapper.addOnPoseAvailableListener(this);
        tangoWrapper.addOnFrameAvailableListener(this);
        active = true;
    }

    public void onPause() {
        active = false;
        tangoWrapper.removeOnTangoReadyListener(renderer::connectToTangoCamera);
        tangoWrapper.removeOnPoseAvailableListener(this);
        tangoWrapper.removeOnFrameAvailableListener(this);
        renderer.disconnectFromTangoCamera();
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    @Override
    public void onPoseAvailable(TangoPoseData pose) {
        // NOTE: Invoked by Tango's thread that is not UI one.

        if (pose.baseFrame == TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION &&
            pose.targetFrame == TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE) {

            double timeDelta = (pose.timestamp - prevAd2SsPoseTimestamp) * SECS_TO_MILLISECS;
            prevAd2SsPoseTimestamp = pose.timestamp;

            timeToNextUpdateAd2SsPose -= timeDelta;

            if (timeToNextUpdateAd2SsPose < 0) {
                timeToNextUpdateAd2SsPose = UPDATE_DEBUG_CONSOLE_INTERVAL;

                handlerMain.post(() -> {
                    debugModel.ad2SsTranslation.x = pose.translation[0];
                    debugModel.ad2SsTranslation.y = pose.translation[1];
                    debugModel.ad2SsTranslation.z = pose.translation[2];
                    view.updateDebugModel(debugModel);
                });
            }

            if (pose.statusCode == TangoPoseData.POSE_VALID) {
                if (localizationState == LocalizationState.UNKNOWN ||
                    localizationState == LocalizationState.NOT_LOCALIZED) {
                    LOG.d("Localized.");
                    localizationState = LocalizationState.LOCALIZED;

                    handlerMain.post(() -> {
                        debugModel.localized = true;
                        view.updateDebugModel(debugModel);
                    });
                }
            } else {
                if (localizationState == LocalizationState.UNKNOWN ||
                    localizationState == LocalizationState.LOCALIZED) {
                    LOG.d("Not localized.");
                    localizationState = LocalizationState.NOT_LOCALIZED;

                    handlerMain.post(() -> {
                        debugModel.localized = false;
                        view.updateDebugModel(debugModel);
                    });
                }
            }
        }

        if (pose.baseFrame == TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION &&
            pose.targetFrame == TangoPoseData.COORDINATE_FRAME_DEVICE) {

            double timeDelta = (pose.timestamp - prevAd2DPoseTimestamp) * SECS_TO_MILLISECS;
            prevAd2DPoseTimestamp = pose.timestamp;

            timeToNextUpdateAd2DPose -= timeDelta;

            if (timeToNextUpdateAd2DPose < 0) {
                timeToNextUpdateAd2DPose = UPDATE_DEBUG_CONSOLE_INTERVAL;

                handlerMain.post(() -> {
                    debugModel.ad2DTranslation.x = pose.translation[0];
                    debugModel.ad2DTranslation.y = pose.translation[1];
                    debugModel.ad2DTranslation.z = pose.translation[2];
                    view.updateDebugModel(debugModel);
                });
            }
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
        view.setObjectMenuVisible(hasPickedObject);
    }

    public void onClickFabToggleModelPane() {
        isModelPaneVisible = !isModelPaneVisible;
        view.setTextureModelPaneVisible(isModelPaneVisible);
    }

    public void onClickImageButtonAddModel() {
        view.showEditTextureFragment(null);
    }

    public void onTouchButtonTranslateObject() {
        objectEditMode = ObjectEditMode.TRANSLATE;
        renderer.setObjectEditMode(objectEditMode);

        view.setTranslateObjectSelected(true);
        view.setTranslateObjectMenuVisible(true);
        view.setRotateObjectSelected(false);
        view.setRotateObjectMenuVisible(false);
        view.setScaleObjectSelected(false);
    }

    public void onTouchButtonRotateObject() {
        objectEditMode = ObjectEditMode.ROTATE;
        renderer.setObjectEditMode(objectEditMode);

        view.setTranslateObjectSelected(false);
        view.setTranslateObjectMenuVisible(false);
        view.setRotateObjectSelected(true);
        view.setRotateObjectMenuVisible(true);
        view.setScaleObjectSelected(false);
    }

    public void onTouchButtonTranslateObjectAxis(Axis axis) {
        renderer.setTranslateObjectAxis(axis);

        view.setTranslateObjectAxisSelected(Axis.X, axis == Axis.X);
        view.setTranslateObjectAxisSelected(Axis.Y, axis == Axis.Y);
        view.setTranslateObjectAxisSelected(Axis.Z, axis == Axis.Z);
    }

    public void onTouchButtonRotateObjectAxis(Axis axis) {
        renderer.setRotateObjectAxis(axis);

        view.setRotateObjectAxisSelected(Axis.X, axis == Axis.X);
        view.setRotateObjectAxisSelected(Axis.Y, axis == Axis.Y);
        view.setRotateObjectAxisSelected(Axis.Z, axis == Axis.Z);
    }

    public void onTouchButtonScaleObject() {
        objectEditMode = ObjectEditMode.SCALE;
        renderer.setObjectEditMode(objectEditMode);

        view.setTranslateObjectSelected(false);
        view.setTranslateObjectMenuVisible(false);
        view.setRotateObjectSelected(false);
        view.setRotateObjectMenuVisible(false);
        view.setScaleObjectSelected(true);
    }

    public int getModelCount() {
        return models.size();
    }

    public void onCreateItemView(@NonNull TextureModelListItemView itemView) {
        ModelItemPresenter itemPresenter = new ModelItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
        selection.addItemPresenter(itemPresenter);
    }

    public void onDropModel() {
        if (0 <= selection.selectedPosition) {
            TextureModel model = models.get(selection.selectedPosition);
            renderer.addPlaneBitmap(model.bitmap);
        }
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

    public final class ModelItemPresenter {

        private TextureModelListItemView itemView;

        public void onCreateItemView(@NonNull TextureModelListItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            TextureModel model = models.get(position);
            itemView.showModel(model);
        }

        public void onLoadBitmap(int position) {
            TextureModel model = models.get(position);

            Disposable disposable = findUserTextureBitmapUseCase
                    .execute(model.textureId, (totalBytes, bytesTransferred) -> {
                        // Update the progress bar.
                        itemView.showProgress((int) totalBytes, (int) bytesTransferred);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(() -> itemView.hideProgress())
                    .subscribe(bitmap -> {
                        // Set the bitmap into the model.
                        model.bitmap = bitmap;
                        // Redraw.
                        itemView.showModel(model);
                    }, e -> {
                        // TODO: How to recover.
                        LOG.w(String.format("Failed to load the user texture bitmap: textureId = %s", model.textureId),
                              e);
                    });
            compositeDisposable.add(disposable);
        }

        public void onClickViewTop(int position) {
            selection.setSelectedPosition(position);
        }

        public void onLongClickViewTop(int position) {
            selection.setSelectedPosition(position);
            itemView.startDrag();
        }

        public void onClickImageButtonEditTexture(int position) {
            TextureModel model = models.get(position);
            view.showEditTextureFragment(model.textureId);
        }

        public void onClickImageButtonDeleteTexture(int position) {
            itemView.showDeleteTextureConfirmationDialog();
        }

        public void onDelete(int position) {
            TextureModel model = models.get(position);

            Disposable disposable = deleteUserTextureUseCase
                    .execute(model.textureId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        models.remove(position);
                        view.updateTextureModelPane();
                        view.showSnackbar(R.string.snackbar_done);
                    }, e -> {
                        LOG.e(String.format("Failed to delete the user texture: textureId = %s", model.textureId), e);
                    });
            compositeDisposable.add(disposable);
        }

        void setSelected(int selectedPosition, boolean selected) {
            itemView.setSelected(selectedPosition, selected);
        }
    }

    private final class SingleSelection {

        int selectedPosition = -1;

        Set<ModelItemPresenter> itemPresenters = new HashSet<>();

        void addItemPresenter(ModelItemPresenter itemPresenter) {
            itemPresenters.add(itemPresenter);
        }

        void setSelectedPosition(int selectedPosition) {
            if (0 <= this.selectedPosition) {
                // Deselect the previous selection.
                for (ModelItemPresenter itemPresenter : itemPresenters) {
                    itemPresenter.setSelected(this.selectedPosition, false);
                }
            }

            if (this.selectedPosition == selectedPosition) {
                // Deselect only.
                this.selectedPosition = -1;
            } else if (0 <= selectedPosition) {
                // Select the new position.
                this.selectedPosition = selectedPosition;
                for (ModelItemPresenter itemPresenter : itemPresenters) {
                    itemPresenter.setSelected(this.selectedPosition, true);
                }
            }
        }
    }

    private enum LocalizationState {
        UNKNOWN,
        LOCALIZED,
        NOT_LOCALIZED
    }
}
