package com.lakeel.altla.vision.builder.presentation.view.fragment;


import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tango.ux.TangoUxLayout;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.model.Axis;
import com.lakeel.altla.vision.builder.presentation.model.SceneBuildModel;
import com.lakeel.altla.vision.builder.presentation.presenter.UserSceneBuildPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserSceneBuildView;
import com.lakeel.altla.vision.presentation.view.fragment.AbstractFragment;

import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.TextureView;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public final class UserSceneBuildFragment extends AbstractFragment<UserSceneBuildView, UserSceneBuildPresenter>
        implements UserSceneBuildView {

    @Inject
    UserSceneBuildPresenter presenter;

    @BindView(R.id.view_top)
    ViewGroup viewTop;

    @BindView(R.id.layout_tango_ux)
    TangoUxLayout tangoUxLayout;

    @BindView(R.id.texture_view)
    TextureView textureView;

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

    private InteractionListener interactionListener;

    @NonNull
    public static Fragment newInstance(@NonNull SceneBuildModel sceneBuildModel) {
        UserSceneBuildFragment fragment = new UserSceneBuildFragment();
        Bundle bundle = UserSceneBuildPresenter.createArguments(sceneBuildModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected UserSceneBuildPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserSceneBuildView getViewInterface() {
        return this;
    }

    @Override
    protected void onAttachOverride(@NonNull Context context) {
        super.onAttachOverride(context);

        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
        interactionListener = InteractionListener.class.cast(context);

        gestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                getLog().d("onDown");
                // Must return true to receive motion events on onScroll.
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                getLog().d("onScroll");
                return presenter.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                getLog().d("onSingleTapUp");
                return presenter.onSingleTapUp(e);
            }
        });
    }

    @Override
    protected void onDetachOverride() {
        super.onDetachOverride();

        interactionListener = null;
    }

    @Nullable
    @Override
    protected View onCreateViewCore(LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_scene_build, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        textureView.setFrameRate(60d);
        textureView.setRenderMode(ISurface.RENDERMODE_WHEN_DIRTY);
        textureView.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
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
                    presenter.onDropModel(event.getClipData());
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    return true;
            }

            return false;
        });
        textureView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        UserActorImageListFragment userActorImageListFragment = UserActorImageListFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                                 .add(R.id.user_actor_image_list_container,
                                      userActorImageListFragment,
                                      UserActorImageListFragment.class.getName())
                                 .commit();

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_scene_build, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_debug:
                presenter.onToggleDebug();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResumeOverride() {
        super.onResumeOverride();

        textureView.onResume();
    }

    @Override
    protected void onPauseOverride() {
        super.onPauseOverride();

        textureView.onPause();
    }

    @Override
    public void setTangoUxLayout(TangoUx tangoUx) {
        tangoUx.setLayout(tangoUxLayout);
    }

    @Override
    public void setSurfaceRenderer(ISurfaceRenderer renderer) {
        textureView.setSurfaceRenderer(renderer);
    }

    @Override
    public void onUpdateObjectMenuVisible(boolean visible) {
        if (visible) {
            viewGroupObjectMenu.setVisibility(View.VISIBLE);
        } else {
            viewGroupObjectMenu.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUpdateTranslateObjectSelected(boolean selected) {
        buttonTranslateObject.setPressed(selected);
    }

    @Override
    public void onUpdateTranslateObjectMenuVisible(boolean visible) {
        viewGroupTranslateObjectMenu.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onUpdateTranslateObjectAxisSelected(Axis axis, boolean selected) {
        buttonsTranslateObjectAxes[axis.getValue()].setPressed(selected);
    }

    @Override
    public void onUpdateRotateObjectSelected(boolean selected) {
        buttonRotateObject.setPressed(selected);
    }

    @Override
    public void onUpdateRotateObjectMenuVisible(boolean visible) {
        viewGroupRotateObjectMenu.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onUpdateRotateObjectAxisSelected(Axis axis, boolean selected) {
        buttonsRotateObjectAxes[axis.getValue()].setPressed(selected);
    }

    @Override
    public void onUpdateScaleObjectSelected(boolean selected) {
        buttonScaleObject.setPressed(selected);
    }

    @Override
    public void onUpdateDebugConsoleVisible(boolean visible) {
        final String tag = DebugConsoleFragment.class.getName();

        DebugConsoleFragment fragment = (DebugConsoleFragment) getChildFragmentManager().findFragmentByTag(tag);

        if (visible) {
            if (fragment == null) {
                fragment = DebugConsoleFragment.newInstance();
                getChildFragmentManager().beginTransaction()
                                         .add(R.id.debug_console_container, fragment, tag)
                                         .commit();
            }
        } else {
            getChildFragmentManager().beginTransaction()
                                     .remove(fragment)
                                     .commit();
        }
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
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

    public interface InteractionListener {

    }
}
