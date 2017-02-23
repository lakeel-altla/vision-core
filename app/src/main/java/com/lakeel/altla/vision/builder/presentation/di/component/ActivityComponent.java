package com.lakeel.altla.vision.builder.presentation.di.component;

import com.lakeel.altla.vision.builder.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.builder.presentation.di.module.AndroidRepositoryModule;
import com.lakeel.altla.vision.builder.presentation.di.module.FirebaseRepositoryModule;
import com.lakeel.altla.vision.builder.presentation.di.module.GoogleApiModule;
import com.lakeel.altla.vision.builder.presentation.view.activity.MainActivity;
import com.lakeel.altla.vision.builder.presentation.view.fragment.DebugConsoleFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.ProjectFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.SignInFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.TangoPermissionFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserActorImageListFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserAreaDescriptionListInAreaFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserAreaListFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserSceneBuildFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserSceneListInAreaFragmentInArea;
import com.lakeel.altla.vision.di.ActivityScope;

import dagger.Subcomponent;

/**
 * Defines the dagger component that manages objects per activity.
 */
@ActivityScope
@Subcomponent(modules = { ActivityModule.class,
                          GoogleApiModule.class,
                          AndroidRepositoryModule.class,
                          FirebaseRepositoryModule.class })
public interface ActivityComponent {

    void inject(MainActivity activity);

    void inject(SignInFragment fragment);

    void inject(TangoPermissionFragment fragment);

    void inject(ProjectFragment fragment);

    void inject(UserAreaListFragment fragment);

    void inject(UserAreaDescriptionListInAreaFragment fragment);

    void inject(UserSceneListInAreaFragmentInArea fragment);

    void inject(UserSceneBuildFragment fragment);

    void inject(UserActorImageListFragment fragment);

    void inject(DebugConsoleFragment fragment);
}
