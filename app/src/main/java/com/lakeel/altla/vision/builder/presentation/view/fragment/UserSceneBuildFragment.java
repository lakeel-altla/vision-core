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
import butterknife.OnClick;
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

    @BindView(R.id.view_group_edit_user_actor_menu)
    ViewGroup viewGroupEditUserActorMenu;

    @BindView(R.id.view_group_translate_menu)
    ViewGroup viewGroupTranslateMenu;

    @BindView(R.id.view_group_rotate_menu)
    ViewGroup viewGroupRotateMenu;

    @BindView(R.id.button_translate)
    Button buttonTranslate;

    @BindViews({ R.id.button_translate_in_x_axis, R.id.button_translate_in_y_axis,
                 R.id.button_translate_in_z_axis })
    Button[] buttonsTranslateAxes;

    @BindView(R.id.button_rotate)
    Button buttonRotate;

    @BindViews({ R.id.button_rotate_in_x_axis, R.id.button_rotate_object_in_y_axis,
                 R.id.button_rotate_object_in_z_axis })
    Button[] buttonsRotateAxes;

    @BindView(R.id.button_scale)
    Button buttonScale;

    @BindView(R.id.button_detail)
    Button buttonEdit;

    private GestureDetectorCompat gestureDetector;

    private boolean scrolling;

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
                scrolling = true;
                return presenter.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                getLog().d("onSingleTapUp");
                return presenter.onSingleTapUp(e);
            }
        });

        textureView.setOnTouchListener((v, event) -> {
            if (gestureDetector.onTouchEvent(event)) {
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (scrolling) {
                    scrolling = false;
                    presenter.onScrollFinished(event);
                }
            }

            return false;
        });

        UserAssetImageListFragment userActorImageListFragment = UserAssetImageListFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                                 .add(R.id.user_actor_image_list_container,
                                      userActorImageListFragment,
                                      UserAssetImageListFragment.class.getName())
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
            case R.id.action_close:
                presenter.onActionCloseSelected();
                return true;
            case R.id.action_debug:
                presenter.onActionDebugSelected();
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
            viewGroupEditUserActorMenu.setVisibility(View.VISIBLE);
        } else {
            viewGroupEditUserActorMenu.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUpdateTranslateSelected(boolean selected) {
        buttonTranslate.setPressed(selected);
    }

    @Override
    public void onUpdateTranslateMenuVisible(boolean visible) {
        viewGroupTranslateMenu.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onUpdateTranslateAxisSelected(Axis axis, boolean selected) {
        buttonsTranslateAxes[axis.getValue()].setPressed(selected);
    }

    @Override
    public void onUpdateRotateSelected(boolean selected) {
        buttonRotate.setPressed(selected);
    }

    @Override
    public void onUpdateRotateMenuVisible(boolean visible) {
        viewGroupRotateMenu.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onUpdateRotateAxisSelected(Axis axis, boolean selected) {
        buttonsRotateAxes[axis.getValue()].setPressed(selected);
    }

    @Override
    public void onUpdateScaleSelected(boolean selected) {
        buttonScale.setPressed(selected);
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
    public void onShowUserActorView(@NonNull String sceneId, @NonNull String actorId) {
        interactionListener.onShowUserActorView(sceneId, actorId);
    }

    @Override
    public void onCloseView() {
        interactionListener.onCloseUserSceneBuildView();
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

    @OnTouch(R.id.button_translate)
    boolean onTouchButtonTranslate(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonTranslate.setPressed(true);
            presenter.onTouchButtonTranslate();
        }
        return true;
    }

    @OnTouch(R.id.button_translate_in_x_axis)
    boolean onTouchButtonTranslateInXAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonsTranslateAxes[Axis.X.getValue()].setPressed(true);
            presenter.onTouchButtonTranslateAxis(Axis.X);
        }
        return true;
    }

    @OnTouch(R.id.button_translate_in_y_axis)
    boolean onTouchButtonTranslateInYAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonsTranslateAxes[Axis.Y.getValue()].setPressed(true);
            presenter.onTouchButtonTranslateAxis(Axis.Y);
        }
        return true;
    }

    @OnTouch(R.id.button_translate_in_z_axis)
    boolean onTouchButtonTranslateInZAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonsTranslateAxes[Axis.Z.getValue()].setPressed(true);
            presenter.onTouchButtonTranslateAxis(Axis.Z);
        }
        return true;
    }

    @OnTouch(R.id.button_rotate)
    boolean onTouchButtonRotateObject(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonRotate.setPressed(true);
            presenter.onTouchButtonRotateObject();
        }
        return true;
    }

    @OnTouch(R.id.button_rotate_in_x_axis)
    boolean onTouchButtonRotateInXAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonsRotateAxes[Axis.X.getValue()].setPressed(true);
            presenter.onTouchButtonRotateAxis(Axis.X);
        }
        return true;
    }

    @OnTouch(R.id.button_rotate_object_in_y_axis)
    boolean onTouchButtonRotateInYAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonsRotateAxes[Axis.Y.getValue()].setPressed(true);
            presenter.onTouchButtonRotateAxis(Axis.Y);
        }
        return true;
    }

    @OnTouch(R.id.button_rotate_object_in_z_axis)
    boolean onTouchButtonRotateInZAxis(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonsRotateAxes[Axis.Z.getValue()].setPressed(true);
            presenter.onTouchButtonRotateAxis(Axis.Z);
        }
        return true;
    }

    @OnTouch(R.id.button_scale)
    boolean onTouchButtonScale(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonScale.setPressed(true);
            presenter.onTouchButtonScale();
        }
        return true;
    }

    @OnTouch(R.id.button_detail)
    boolean onTouchButtonEdit(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            buttonEdit.setPressed(true);
            presenter.onTouchButtonDetail();
        }
        return true;
    }

    @OnClick(R.id.button_delete)
    void onClickButtonDelete() {
        presenter.onClickButtonDelete();
    }

    public interface InteractionListener {

        void onShowUserActorView(@NonNull String sceneId, @NonNull String actorId);

        void onCloseUserSceneBuildView();
    }
}
