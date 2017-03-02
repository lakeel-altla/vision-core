package com.lakeel.altla.vision.builder.presentation.view.activity;

import com.google.atap.tangoservice.Tango;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.app.MyApplication;
import com.lakeel.altla.vision.builder.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.builder.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.builder.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.builder.presentation.model.SceneBuildModel;
import com.lakeel.altla.vision.builder.presentation.view.fragment.ProjectFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.SignInFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.TangoPermissionFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserActorFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserAreaDescriptionListInAreaFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserAreaListFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserSceneBuildFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserSceneListInAreaFragmentInArea;
import com.lakeel.altla.vision.domain.usecase.ObserveConnectionUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveUserProfileUseCase;
import com.lakeel.altla.vision.domain.usecase.SignOutUseCase;
import com.squareup.picasso.Picasso;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class MainActivity extends AppCompatActivity
        implements FragmentManager.OnBackStackChangedListener,
                   ActivityScopeContext,
                   TangoWrapper.OnTangoReadyListener,
                   SignInFragment.InteractionListener,
                   TangoPermissionFragment.InteractionListener,
                   ProjectFragment.InteractionListener,
                   UserAreaListFragment.InteractionListener,
                   UserAreaDescriptionListInAreaFragment.InteractionListener,
                   UserSceneListInAreaFragmentInArea.InteractionListener,
                   UserSceneBuildFragment.InteractionListener,
                   UserActorFragment.InteractionListener,
                   NavigationView.OnNavigationItemSelectedListener {

    private static final Log LOG = LogFactory.getLog(MainActivity.class);

    @Inject
    ObserveUserProfileUseCase observeUserProfileUseCase;

    @Inject
    ObserveConnectionUseCase observeConnectionUseCase;

    @Inject
    SignOutUseCase signOutUseCase;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ActivityComponent activityComponent;

    private ActionBarDrawerToggle drawerToggle;

    private NavigationViewHeader navigationViewHeader;

    private Disposable observeUserProfileDisposable;

    private Disposable observeConnectionDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // NOTE:
        //
        // Any injection must be done before super.onCreate()
        // because fragments are already attached to an activity when they are resumed or instant-run.
        activityComponent = MyApplication.getApplicationComponent(this)
                                         .activityComponent(new ActivityModule(this));
        activityComponent.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        // setDisplayHomeAsUpEnabled(true) will cause the arrow icon to appear instead of the hamburger icon
        // by calling drawerToggle.setDrawerIndicatorEnabled(false).
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Use the following constructor to set the Toolbar as the ActionBar of an Activity.
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        updateActionBarHome();

        navigationView.setNavigationItemSelectedListener(this);
        navigationViewHeader = new NavigationViewHeader(navigationView);

        if (savedInstanceState == null) {
            showSignInFragment();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(navigationViewHeader);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unsubscribe the connection.
        if (observeConnectionDisposable != null) {
            observeConnectionDisposable.dispose();
            observeConnectionDisposable = null;
        }

        // Unsubscribe the user profile.
        if (observeUserProfileDisposable != null) {
            observeUserProfileDisposable.dispose();
            observeUserProfileDisposable = null;
        }

        compositeDisposable.clear();

        FirebaseAuth.getInstance().removeAuthStateListener(navigationViewHeader);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (0 < getSupportFragmentManager().getBackStackEntryCount()) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_sign_out:
                onSignOut();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackStackChanged() {
        updateActionBarHome();
    }

    @Override
    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    @Override
    public void onTangoReady(Tango tango) {
        LOG.d("Tango is ready.");
    }

    @Override
    public void onCloseSignInView() {
        toolbar.setVisibility(View.INVISIBLE);

        replaceFragment(TangoPermissionFragment.newInstance());
    }

    @Override
    public void onCloseTangoPermissionView() {
        toolbar.setVisibility(View.VISIBLE);

        replaceFragment(ProjectFragment.newInstance());
    }

    @Override
    public void onShowUserAreaListView() {
        replaceFragmentAndAddToBackStack(UserAreaListFragment.newInstance());
    }

    @Override
    public void onShowUserAreaDescriptionListInAreaView(@NonNull String areaId) {
        replaceFragmentAndAddToBackStack(UserAreaDescriptionListInAreaFragment.newInstance(areaId));
    }

    @Override
    public void onShowUserSceneListInAreaView(@NonNull String areaId) {
        replaceFragmentAndAddToBackStack(UserSceneListInAreaFragmentInArea.newInstance(areaId));
    }

    @Override
    public void onShowUserActorView(@NonNull String sceneId, @NonNull String actorId) {
        replaceFragmentAndAddToBackStack(UserActorFragment.newInstance(sceneId, actorId));
    }

    @Override
    public void onCloseUserSceneBuildView() {
        replaceFragment(ProjectFragment.newInstance());
    }

    @Override
    public void onShowUserActorEditView(@NonNull String actorId) {
        // TODO
    }

    @Override
    public void onShowUserSceneBuildView(@NonNull SceneBuildModel sceneBuildModel) {
        replaceFragment(UserSceneBuildFragment.newInstance(sceneBuildModel));
    }

    @Override
    public void onUserAreaSelected(@NonNull String areaId) {
        getSupportFragmentManager().popBackStack();

        ProjectFragment fragment = (ProjectFragment) findFragment(ProjectFragment.class);
        if (fragment != null) {
            fragment.onUserAreaSelected(areaId);
        }
    }

    @Override
    public void onUserAreaDescriptionSelected(@NonNull String areaDescriptionId) {
        getSupportFragmentManager().popBackStack();

        ProjectFragment fragment = (ProjectFragment) findFragment(ProjectFragment.class);
        if (fragment != null) {
            fragment.onUserAreaDescriptionSelected(areaDescriptionId);
        }
    }

    @Override
    public void onUserSceneSelected(@NonNull String sceneId) {
        getSupportFragmentManager().popBackStack();

        ProjectFragment fragment = (ProjectFragment) findFragment(ProjectFragment.class);
        if (fragment != null) {
            fragment.onUserSceneSelected(sceneId);
        }
    }

    private void updateActionBarHome() {
        if (getSupportActionBar() != null) {
            boolean isHome = (getSupportFragmentManager().getBackStackEntryCount() == 0);
            drawerToggle.setDrawerIndicatorEnabled(isHome);
        }
    }

    private void onSignOut() {
        Disposable disposable = signOutUseCase
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showSignInFragment);
        compositeDisposable.add(disposable);
    }

    private void showSignInFragment() {
        toolbar.setVisibility(View.INVISIBLE);

        replaceFragment(SignInFragment.newInstance());
    }

    private Fragment findFragment(Class<?> clazz) {
        return getSupportFragmentManager().findFragmentByTag(clazz.getName());
    }

    private void replaceFragmentAndAddToBackStack(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                                   .addToBackStack(null)
                                   .replace(R.id.fragment_container, fragment, fragment.getClass().getName())
                                   .commit();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragment_container, fragment, fragment.getClass().getName())
                                   .commit();
    }

    class NavigationViewHeader implements FirebaseAuth.AuthStateListener {

        @BindView(R.id.image_view_user_photo)
        ImageView imageViewUserPhoto;

        @BindView(R.id.text_view_user_name)
        TextView textViewUserName;

        @BindView(R.id.text_view_user_email)
        TextView textViewUserEmail;

        private NavigationViewHeader(@NonNull NavigationView navigationView) {
            ButterKnife.bind(this, navigationView.getHeaderView(0));
        }

        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                // Subscribe the connection.
                if (observeConnectionDisposable == null) {
                    observeConnectionDisposable = observeConnectionUseCase
                            .execute()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                }

                // Subscribe the user profile.
                if (observeUserProfileDisposable == null) {
                    observeUserProfileDisposable = observeUserProfileUseCase
                            .execute()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(userProfile -> {
                                // Update UI each time the user profile is updated.
                                if (userProfile.photoUri != null) {
                                    Uri photoUri = Uri.parse(userProfile.photoUri);
                                    Picasso.with(MainActivity.this).load(photoUri).into(imageViewUserPhoto);
                                }
                                textViewUserName.setText(userProfile.displayName);
                                textViewUserEmail.setText(userProfile.email);
                            });
                }
            } else {
                // Unsubscribe the connection.
                if (observeConnectionDisposable != null) {
                    observeConnectionDisposable.dispose();
                    observeConnectionDisposable = null;
                }

                // Unsubscribe the user profile.
                if (observeUserProfileDisposable != null) {
                    observeUserProfileDisposable.dispose();
                    observeUserProfileDisposable = null;
                }

                // Clear UI.
                imageViewUserPhoto.setImageBitmap(null);
                textViewUserName.setText(null);
                textViewUserEmail.setText(null);
            }
        }
    }
}
