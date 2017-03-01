package com.lakeel.altla.vision.builder.presentation.view.renderer;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.rajawali.TangoCameraRenderer;
import com.lakeel.altla.vision.builder.presentation.graphics.BitmapPlaneFactory;
import com.lakeel.altla.vision.builder.presentation.graphics.XyzAxesBuilder;
import com.lakeel.altla.vision.builder.presentation.model.EditAxesModel;
import com.lakeel.altla.vision.builder.presentation.model.UserActorImageModel;
import com.lakeel.altla.vision.builder.presentation.model.UserActorModel;
import com.lakeel.altla.vision.domain.model.UserActor;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public final class MainRenderer extends TangoCameraRenderer implements OnObjectPickedListener {

    private static final Log LOG = LogFactory.getLog(MainRenderer.class);

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final Queue<UserActorModel> addUserActorModelQueue = new LinkedList<>();

    private final Queue<UserActorModel> updateUserActorModelQueue = new LinkedList<>();

    private final Queue<String> deleteUserActorIdQueue = new LinkedList<>();

    private final BitmapPlaneFactory bitmapPlaneFactory = new BitmapPlaneFactory();

    private final Object editAxesModelLock = new Object();

    private final Map<Object3D, UserActorModel> userActorModelMap = new HashMap<>();

    private final Map<String, Object3D> object3DMap = new HashMap<>();

    private ObjectColorPicker objectColorPicker;

    private Line3D axes;

    private Object3D pickedObject;

    private EditAxesModel editAxesModel;

    private OnUserActorPickedListener onUserActorPickedListener;

    private OnCurrentCameraTransformUpdatedListener onCurrentCameraTransformUpdatedListener;

    public MainRenderer(Context context) {
        super(context);
    }

    @Override
    protected void initSceneOverride() {
        super.initSceneOverride();

        objectColorPicker = new ObjectColorPicker(this);
        objectColorPicker.setOnObjectPickedListener(this);

        // Build axes model indicating a pose of a picked object.
        axes = new XyzAxesBuilder().setThickness(5)
                                   .setLength(0.25f)
                                   .build();
        // Shift the axes model to a little camera beside.
        axes.setPosition(0, 0, -0.001f);
        axes.setVisible(false);
        getCurrentScene().addChild(axes);
    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        // NOTE:
        //
        // Adding primitives into a scene in a non-OpenGL thread fail,
        // because its constructors access OpenGL vertex buffers etc.
        // So we queue a data indicating a primitive in a non-OpenGL,
        // Renderer#onRender invoked in OpenGL thread instantiates an actual primitive.
        //

        // Add user actors.
        synchronized (addUserActorModelQueue) {
            while (true) {
                UserActorModel model = addUserActorModelQueue.poll();
                if (model == null) {
                    break;
                }

                Object3D object3D;
                if (model instanceof UserActorImageModel) {
                    object3D = bitmapPlaneFactory.create(((UserActorImageModel) model).bitmap);
                    object3D.setPosition(model.position);
                    object3D.setOrientation(model.orientation);
                    object3D.setScale(model.scale);
                } else {
                    LOG.e("Unknown UserActorModel sub-class: " + model.getClass().getName());
                    continue;
                }

                getCurrentScene().addChild(object3D);
                objectColorPicker.registerObject(object3D);
                userActorModelMap.put(object3D, model);
                object3DMap.put(model.actorId, object3D);
            }
        }

        // Update user actors.
        synchronized (updateUserActorModelQueue) {
            while (true) {
                UserActorModel model = updateUserActorModelQueue.poll();
                if (model == null) {
                    break;
                }

                Object3D object3D = object3DMap.get(model.actorId);
                if (object3D == null) {
                    LOG.e("Object3D not found: actorId = %s", model.actorId);
                    continue;
                }

                object3D.setPosition(model.position);
                object3D.setOrientation(model.orientation);
                object3D.setScale(model.scale);
            }
        }

        // Update axes for edit.
        synchronized (editAxesModelLock) {
            if (editAxesModel != null) {
                axes.setPosition(editAxesModel.position);
                axes.setOrientation(editAxesModel.orientation);

                editAxesModel = null;
            }
        }

        // Delete user actors.
        synchronized (deleteUserActorIdQueue) {
            while (true) {
                String actorId = deleteUserActorIdQueue.poll();
                if (actorId == null) {
                    break;
                }

                Object3D object3D = object3DMap.remove(actorId);
                if (object3D == null) {
                    continue;
                }

                userActorModelMap.remove(object3D);

                getCurrentScene().removeChild(object3D);

                if (pickedObject == object3D) {
                    onNoObjectPicked();
                }
            }
        }

        super.onRender(ellapsedRealtime, deltaTime);
    }

    @Override
    public void onObjectPicked(@NonNull Object3D object) {
        if (pickedObject == object) {
            // Unpick.
            changePickedObject(null);
        } else {
            // Pick.
            changePickedObject(object);
        }
    }

    @Override
    public void onNoObjectPicked() {
        // Unpick
        changePickedObject(null);
    }

    @Override
    protected void onCurrentCameraTransformUpdated(double timestamp) {
        super.onCurrentCameraTransformUpdated(timestamp);

        Camera camera = getCurrentCamera();
        Vector3 position = camera.getPosition();
        Quaternion orientation = camera.getOrientation();
        Vector3 forward = getCurrentCameraForward();

        raiseOnCurrentCameraTransformUpdated(position.x, position.y, position.z,
                                             orientation.x, orientation.y, orientation.z, orientation.w,
                                             forward.x, forward.y, forward.z);
    }

    // This method must be invoke on the main thread.
    public void setOnCurrentCameraTransformUpdatedListener(
            OnCurrentCameraTransformUpdatedListener onCurrentCameraTransformUpdatedListener) {
        this.onCurrentCameraTransformUpdatedListener = onCurrentCameraTransformUpdatedListener;
    }

    // This method must be invoke on the main thread.
    public void setOnUserActorPickedListener(@Nullable OnUserActorPickedListener onUserActorPickedListener) {
        this.onUserActorPickedListener = onUserActorPickedListener;
    }

    public void addUserActorModel(@NonNull UserActorModel userActorModel) {
        synchronized (addUserActorModelQueue) {
            addUserActorModelQueue.add(userActorModel);
        }
    }

    public void updateUserActorModel(@NonNull UserActorModel userActorModel) {
        synchronized (updateUserActorModelQueue) {
            updateUserActorModelQueue.add(userActorModel);
        }
    }

    public void updateEditAxesModel(@NonNull EditAxesModel editAxesModel) {
        synchronized (editAxesModelLock) {
            this.editAxesModel = editAxesModel;
        }
    }

    public void removeUserActor(UserActor userActor) {
        synchronized (deleteUserActorIdQueue) {
            deleteUserActorIdQueue.add(userActor.actorId);
        }
    }

    public void tryPickObject(float x, float y) {
        LOG.d("tryPickObject: x = %f, y = %f", x, y);
        objectColorPicker.getObjectAt(x, y);
    }

    private void changePickedObject(@Nullable Object3D object) {
        pickedObject = object;

        UserActorModel pickedUserActor = null;

        if (pickedObject != null) {
            // The axes model uses the same pose with the picked object.
            axes.setPosition(pickedObject.getPosition().clone());
            axes.setOrientation(pickedObject.getOrientation().clone());
            axes.setVisible(true);

            pickedUserActor = userActorModelMap.get(pickedObject);
        } else {
            axes.setVisible(false);
        }

        raiseOnUserActorPicked(pickedUserActor);
    }

    private void raiseOnCurrentCameraTransformUpdated(double positionX, double positionY, double positionZ,
                                                      double orientationX, double orientationY, double orientationZ,
                                                      double orientationW,
                                                      double forwardX, double forwardY, double forwardZ) {
        mainHandler.post(() -> {
            if (onCurrentCameraTransformUpdatedListener != null) {
                onCurrentCameraTransformUpdatedListener.onCurrentCameraTransformUpdated(
                        positionX, positionY, positionZ,
                        orientationX, orientationY, orientationZ, orientationW,
                        forwardX, forwardY, forwardZ
                );
            }
        });
    }

    private void raiseOnUserActorPicked(@Nullable UserActorModel userActorModel) {
        mainHandler.post(() -> {
            if (onUserActorPickedListener != null) {
                onUserActorPickedListener.onUserActorPicked(userActorModel);
            }
        });
    }

    public interface OnCurrentCameraTransformUpdatedListener {

        void onCurrentCameraTransformUpdated(double positionX, double positionY, double positionZ,
                                             double orientationX, double orientationY, double orientationZ,
                                             double orientationW,
                                             double forwardX, double forwardY, double forwardZ);
    }

    public interface OnUserActorPickedListener {

        void onUserActorPicked(@Nullable UserActorModel userActorModel);
    }
}
