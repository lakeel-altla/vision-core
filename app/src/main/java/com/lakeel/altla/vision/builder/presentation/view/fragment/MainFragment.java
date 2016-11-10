package com.lakeel.altla.vision.builder.presentation.view.fragment;


import com.google.atap.tango.ux.TangoUx;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.Axis;
import com.lakeel.altla.vision.builder.presentation.presenter.MainPresenter;
import com.lakeel.altla.vision.builder.presentation.view.MainView;
import com.lakeel.altla.vision.builder.presentation.view.activity.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.view.adapter.ModelAdapter;

import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.TextureView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public final class MainFragment extends Fragment implements MainView {

    private static final Log LOG = LogFactory.getLog(MainFragment.class);

    private static final int REQUEST_CODE_ACTION_OPEN_DOCUMENT = 0;

    @Inject
    MainPresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

//    @BindView(R.id.layout_tango_ux)
//    TangoUxLayout tangoUxLayout;

    @BindView(R.id.texture_view)
    TextureView textureView;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.view_group_model_pane)
    ViewGroup viewGroupModelPane;

    @BindView(R.id.fab_toggle_model_pane)
    FloatingActionButton fabToggleModelPane;

    @BindView(R.id.view_group_object_menu)
    ViewGroup viewGroupObjectMenu;

    @BindView(R.id.view_group_translate_object_menu)
    ViewGroup viewGroupTranslateObjectMenu;

    @BindView(R.id.view_group_rotate_object_menu)
    ViewGroup viewGroupRotateObjectMenu;

    @BindView(R.id.button_translate_object)
    Button buttonTranslateObject;

    @BindViews({ R.id.button_translate_object_in_x_axis, R.id.button_translate_object_in_y_axis,
                 R.id.button_translate_object_in_z_axis })
    Button[] buttonsTranslateObjectAxes;

    @BindView(R.id.button_rotate_object)
    Button buttonRotateObject;

    @BindViews({ R.id.button_rotate_object_in_x_axis, R.id.button_rotate_object_in_y_axis,
                 R.id.button_rotate_object_in_z_axis })
    Button[] buttonsRotateObjectAxes;

    @BindView(R.id.button_scale_object)
    Button buttonScaleObject;

    private GestureDetectorCompat gestureDetector;

    private AlertDialog alertDialog;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);

        gestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                LOG.d("onDown");
                // Must return true to receive motion events on onScroll.
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                LOG.d("onScroll");
                return presenter.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                LOG.d("onSingleTapUp");
                return presenter.onSingleTapUp(e);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        presenter.onCreateView(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new ModelAdapter(presenter));

        textureView.setFrameRate(60d);
        textureView.setRenderMode(ISurface.RENDERMODE_WHEN_DIRTY);
        textureView.setOnDragListener((v, dragEvent) -> {
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // returns true to accept a drag event.
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    return true;
                case DragEvent.ACTION_DROP:
                    presenter.onDropModel();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    return true;
            }

            return false;
        });
        textureView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        presenter.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.onResume();
        textureView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        presenter.onPause();
        textureView.onPause();
    }

    @Override
    public void setTangoUxLayout(TangoUx tangoUx) {
//        tangoUx.setLayout(tangoUxLayout);
    }

    @Override
    public void setSurfaceRenderer(ISurfaceRenderer renderer) {
        textureView.setSurfaceRenderer(renderer);
    }

    @Override
    public void requestRender() {
        textureView.requestRenderUpdate();
    }

    @Override
    public void showSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setModelPaneVisible(boolean visible) {
        if (visible) {
            viewGroupModelPane.setVisibility(View.VISIBLE);
            fabToggleModelPane.setImageResource(R.drawable.ic_expand_more_black_24dp);
        } else {
            viewGroupModelPane.setVisibility(View.GONE);
            fabToggleModelPane.setImageResource(R.drawable.ic_expand_less_black_24dp);
        }
    }

    @Override
    public void showSelectImageMethodDialog(@ArrayRes int itemsId) {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.dialog_select_image_methods_title)
                    .setItems(itemsId, (dialogInterface, i) -> {
                        presenter.onSelectImageMethodSelected(i);
                    })
                    .create();
        }
        alertDialog.show();
    }

    @Override
    public void showImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*");

        startActivityForResult(intent, REQUEST_CODE_ACTION_OPEN_DOCUMENT);
    }

    @Override
    public void updateModels() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void setObjectMenuVisible(boolean visible) {
        if (visible) {
            viewGroupObjectMenu.setVisibility(View.VISIBLE);
        } else {
            viewGroupObjectMenu.setVisibility(View.GONE);
        }
    }

    @Override
    public void setTranslateObjectSelected(boolean selected) {
        buttonTranslateObject.setPressed(selected);
    }

    @Override
    public void setTranslateObjectMenuVisible(boolean visible) {
        viewGroupTranslateObjectMenu.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setTranslateObjectAxisSelected(Axis axis, boolean selected) {
        buttonsTranslateObjectAxes[axis.getValue()].setPressed(selected);
    }

    @Override
    public void setRotateObjectSelected(boolean selected) {
        buttonRotateObject.setPressed(selected);
    }

    @Override
    public void setRotateObjectMenuVisible(boolean visible) {
        viewGroupRotateObjectMenu.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setRotateObjectAxisSelected(Axis axis, boolean selected) {
        buttonsRotateObjectAxes[axis.getValue()].setPressed(selected);
    }

    @Override
    public void setScaleObjectSelected(boolean selected) {
        buttonScaleObject.setPressed(selected);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_ACTION_OPEN_DOCUMENT == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                Uri uri = (data != null) ? data.getData() : null;
                if (uri != null) {
                    presenter.onImagePicked(uri);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.image_button_add_model)
    void onClickImageButtonAddModel() {
        presenter.onClickImageButtonAddModel();
    }

    @OnClick(R.id.fab_toggle_model_pane)
    void onClickFabToggleModelPane() {
        presenter.onClickFabToggleModelPane();
    }

    //
    // NOTE:
    //
    // To keep a button pressed, call setPressed(true) and return true in onTouch event handlers
    // instead of an onClick ones.
    //

    @OnTouch(R.id.button_translate_object)
    boolean onTouchButtonTranslateObject(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonTranslateObject.setPressed(true);
            presenter.onTouchButtonTranslateObject();
        }
        return true;
    }

    @OnTouch(R.id.button_translate_object_in_x_axis)
    boolean onTouchButtonTranslateObjectInXAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonsTranslateObjectAxes[Axis.X.getValue()].setPressed(true);
            presenter.onTouchButtonTranslateObjectAxis(Axis.X);
        }
        return true;
    }

    @OnTouch(R.id.button_translate_object_in_y_axis)
    boolean onTouchButtonTranslateObjectInYAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonsTranslateObjectAxes[Axis.Y.getValue()].setPressed(true);
            presenter.onTouchButtonTranslateObjectAxis(Axis.Y);
        }
        return true;
    }

    @OnTouch(R.id.button_translate_object_in_z_axis)
    boolean onTouchButtonTranslateObjectInZAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonsTranslateObjectAxes[Axis.Z.getValue()].setPressed(true);
            presenter.onTouchButtonTranslateObjectAxis(Axis.Z);
        }
        return true;
    }

    @OnTouch(R.id.button_rotate_object)
    boolean onTouchButtonRotateObject(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonRotateObject.setPressed(true);
            presenter.onTouchButtonRotateObject();
        }
        return true;
    }

    @OnTouch(R.id.button_rotate_object_in_x_axis)
    boolean onTouchButtonRotateObjectInXAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonsRotateObjectAxes[Axis.X.getValue()].setPressed(true);
            presenter.onTouchButtonRotateObjectAxis(Axis.X);
        }
        return true;
    }

    @OnTouch(R.id.button_rotate_object_in_y_axis)
    boolean onTouchkButtonRotateObjectInYAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonsRotateObjectAxes[Axis.Y.getValue()].setPressed(true);
            presenter.onTouchButtonRotateObjectAxis(Axis.Y);
        }
        return true;
    }

    @OnTouch(R.id.button_rotate_object_in_z_axis)
    boolean onTouchButtonRotateObjectInZAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonsRotateObjectAxes[Axis.Z.getValue()].setPressed(true);
            presenter.onTouchButtonRotateObjectAxis(Axis.Z);
        }
        return true;
    }

    @OnTouch(R.id.button_scale_object)
    boolean onTouchButtonScaleObject(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonScaleObject.setPressed(true);
            presenter.onTouchButtonScaleObject();
        }
        return true;
    }
}
