package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.vision.builder.presentation.app.MyApplication;
import com.lakeel.altla.vision.domain.helper.CurrentDeviceResolver;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final MyApplication application;

    public ApplicationModule(@NonNull MyApplication application) {
        this.application = application;
    }

    @Named(Names.APPLICATION_CONTEXT)
    @Singleton
    @Provides
    Context provideContext() {
        return application;
    }

    @Singleton
    @Provides
    Resources provideResources() {
        return application.getResources();
    }

    @Singleton
    @Provides
    CurrentDeviceResolver provideCurrentDeviceResolver() {
        return new CurrentDeviceResolver(FirebaseInstanceId.getInstance().getId());
    }
}
