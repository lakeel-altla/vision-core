package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoPoseData;

import com.lakeel.altla.tango.OnFrameAvailableListener;
import com.lakeel.altla.tango.OnPoseAvailableListener;
import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.module.Names;
import com.lakeel.altla.vision.builder.presentation.model.Axis;
import com.lakeel.altla.vision.builder.presentation.model.MainDebugModel;
import com.lakeel.altla.vision.builder.presentation.model.ObjectEditMode;
import com.lakeel.altla.vision.builder.presentation.model.TextureItemModel;
import com.lakeel.altla.vision.builder.presentation.view.MainView;
import com.lakeel.altla.vision.builder.presentation.view.TextureItemView;
import com.lakeel.altla.vision.builder.presentation.view.renderer.MainRenderer;
import com.lakeel.altla.vision.domain.usecase.DeleteUserTextureUseCase;
import com.lakeel.altla.vision.domain.usecase.FindAllUserTexturesUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserTextureBitmapUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

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
import io.reactivex.disposables.Disposable;

/**
 * Defines the presenter for {@link MainView}.
 */
public final class MainPresenter extends BasePresenter<MainView>
        implements OnPoseAvailableListener, OnFrameAvailableListener, MainRenderer.OnPickedObjectChangedListener {

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

    private final List<TextureItemModel> textureItems = new ArrayList<>();

    private final SingleTextureSelection singleTextureSelection = new SingleTextureSelection();

    private final Handler handlerMain = new Handler(Looper.getMainLooper());

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
    public MainPresenter() {
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().setTangoUxLayout(tangoWrapper.getTangoUx());

        renderer = new MainRenderer(context);
        renderer.setOnPickedObjectChangedListener(this);
        getView().setSurfaceRenderer(renderer);

        getView().onUpdateTextureModelPaneVisible(false);
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

        textureItems.clear();
        getView().onTextureItemsUpdated();

        Disposable disposable = findAllUserTexturesUseCase
                .execute()
                .map(userTexture -> new TextureItemModel(userTexture.textureId, userTexture.name))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    textureItems.add(model);
                    getView().onTextureItemInserted(textureItems.size() - 1);
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable);
    }

    @Override
    protected void onResumeOverride() {
        super.onResumeOverride();

        tangoWrapper.addOnTangoReadyListener(renderer::connectToTangoCamera);
        tangoWrapper.addOnPoseAvailableListener(this);
        tangoWrapper.addOnFrameAvailableListener(this);
        active = true;
    }

    @Override
    protected void onPauseOverride() {
        super.onPauseOverride();

        active = false;
        tangoWrapper.removeOnTangoReadyListener(renderer::connectToTangoCamera);
        tangoWrapper.removeOnPoseAvailableListener(this);
        tangoWrapper.removeOnFrameAvailableListener(this);
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

    public void onClickFabToggleModelPane() {
        isModelPaneVisible = !isModelPaneVisible;
        getView().onUpdateTextureModelPaneVisible(isModelPaneVisible);
    }

    public void onClickImageButtonAddModel() {
        getView().onShowEditTextureView(null);
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

    public int getTextureItemCount() {
        return textureItems.size();
    }

    public TextureItemPresenter createTextureItemPresenter() {
        TextureItemPresenter itemPresenter = new TextureItemPresenter();
        singleTextureSelection.addItemPresenter(itemPresenter);
        return itemPresenter;
    }

    public void onDropModel() {
        if (0 <= singleTextureSelection.selectedPosition) {
            TextureItemModel model = textureItems.get(singleTextureSelection.selectedPosition);
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

    public final class TextureItemPresenter {

        private TextureItemView itemView;

        public void onCreateItemView(@NonNull TextureItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            TextureItemModel model = textureItems.get(position);
            itemView.onModelUpdated(model);
        }

        public void onLoadBitmap(int position) {
            TextureItemModel model = textureItems.get(position);

            Disposable disposable = findUserTextureBitmapUseCase
                    .execute(model.textureId, (totalBytes, bytesTransferred) -> {
                        // Update the progress bar.
                        itemView.onShowProgress((int) totalBytes, (int) bytesTransferred);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(() -> itemView.onHideProgress())
                    .subscribe(bitmap -> {
                        // Set the bitmap into the model.
                        model.bitmap = bitmap;
                        // Redraw.
                        itemView.onModelUpdated(model);
                    }, e -> {
                        // TODO: How to recover.
                        getLog().w(String.format("Failed to load the user texture bitmap: textureId = %s",
                                                 model.textureId),
                                   e);
                    });
            manageDisposable(disposable);
        }

        public void onClickViewTop(int position) {
            singleTextureSelection.setSelectedPosition(position);
        }

        public void onLongClickViewTop(int position) {
            singleTextureSelection.setSelectedPosition(position);
            itemView.onStartDrag();
        }

        public void onClickImageButtonEditTexture(int position) {
            TextureItemModel model = textureItems.get(position);
            getView().onShowEditTextureView(model.textureId);
        }

        public void onClickImageButtonDeleteTexture(int position) {
            itemView.onShowDeleteTextureConfirmationDialog();
        }

        public void onDelete(int position) {
            TextureItemModel model = textureItems.get(position);

            Disposable disposable = deleteUserTextureUseCase
                    .execute(model.textureId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        textureItems.remove(position);
                        getView().onTextureItemsUpdated();
                        getView().onSnackbar(R.string.snackbar_done);
                    }, e -> {
                        getLog().e(String.format("Failed to delete the user texture: textureId = %s", model.textureId),
                                   e);
                    });
            manageDisposable(disposable);
        }

        void setSelected(int selectedPosition, boolean selected) {
            itemView.onSelect(selectedPosition, selected);
        }
    }

    private final class SingleTextureSelection {

        int selectedPosition = -1;

        Set<TextureItemPresenter> itemPresenters = new HashSet<>();

        void addItemPresenter(TextureItemPresenter itemPresenter) {
            itemPresenters.add(itemPresenter);
        }

        void setSelectedPosition(int selectedPosition) {
            if (0 <= this.selectedPosition) {
                // Deselect the previous selection.
                for (TextureItemPresenter itemPresenter : itemPresenters) {
                    itemPresenter.setSelected(this.selectedPosition, false);
                }
            }

            if (this.selectedPosition == selectedPosition) {
                // Deselect only.
                this.selectedPosition = -1;
            } else if (0 <= selectedPosition) {
                // Select the new position.
                this.selectedPosition = selectedPosition;
                for (TextureItemPresenter itemPresenter : itemPresenters) {
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
