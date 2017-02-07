package com.lakeel.altla.vision.builder.presentation.di.component;

import com.lakeel.altla.vision.builder.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.builder.presentation.di.module.AndroidRepositoryModule;
import com.lakeel.altla.vision.builder.presentation.di.module.FirebaseRepositoryModule;
import com.lakeel.altla.vision.builder.presentation.di.module.GoogleApiModule;
import com.lakeel.altla.vision.builder.presentation.view.activity.MainActivity;
import com.lakeel.altla.vision.builder.presentation.view.fragment.MainFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.RegisterTextureFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.SignInFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.TangoPermissionFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserAreaDescriptionFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserAreaDescriptionListFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserAreaDescriptionSceneListFragment;
import com.lakeel.altla.vision.builder.presentation.view.fragment.UserAreaListFragment;
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

    void inject(UserAreaListFragment fragment);

    void inject(MainFragment fragment);

    void inject(RegisterTextureFragment fragment);

    void inject(UserAreaDescriptionListFragment fragment);

    void inject(UserAreaDescriptionFragment fragment);

    void inject(UserAreaDescriptionSceneListFragment fragment);
}
