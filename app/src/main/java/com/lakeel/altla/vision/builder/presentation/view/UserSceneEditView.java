package com.lakeel.altla.vision.builder.presentation.view;

import com.google.atap.tango.ux.TangoUx;

import com.lakeel.altla.vision.builder.presentation.model.Axis;
import com.lakeel.altla.vision.builder.presentation.model.MainDebugModel;

import org.rajawali3d.renderer.ISurfaceRenderer;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

/**
 * Defines the main view.
 */
public interface UserSceneEditView {

    void setTangoUxLayout(TangoUx tangoUx);

    void setSurfaceRenderer(ISurfaceRenderer renderer);

    void onUpdateTextureModelPaneVisible(boolean visible);

    void onTextureItemInserted(int position);

    void onTextureItemsUpdated();

    void onUpdateObjectMenuVisible(boolean visible);

    void onUpdateTranslateObjectSelected(boolean selected);

    void onUpdateTranslateObjectMenuVisible(boolean visible);

    void onUpdateTranslateObjectAxisSelected(Axis axis, boolean selected);

    void onUpdateRotateObjectSelected(boolean selected);

    void onUpdateRotateObjectMenuVisible(boolean visible);

    void onUpdateRotateObjectAxisSelected(Axis axis, boolean selected);

    void onUpdateScaleObjectSelected(boolean selected);

    void onShowEditTextureView(@Nullable String id);

    void onDebugModelUpdated(MainDebugModel model);

    void onSnackbar(@StringRes int resId);
}
