package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoPoseData;

import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.di.ActivityScope;

import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class ActivityModule {

    private static final List<TangoCoordinateFramePair> FRAME_PAIRS;

    private final AppCompatActivity activity;

    static {
        FRAME_PAIRS = new ArrayList<>();

        FRAME_PAIRS.add(new TangoCoordinateFramePair(TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                                                     TangoPoseData.COORDINATE_FRAME_DEVICE));

        FRAME_PAIRS.add(new TangoCoordinateFramePair(TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION,
                                                     TangoPoseData.COORDINATE_FRAME_DEVICE));

        FRAME_PAIRS.add(new TangoCoordinateFramePair(TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION,
                                                     TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE));
    }

    public ActivityModule(@NonNull AppCompatActivity activity) {
        this.activity = activity;
    }

    @ActivityScope
    @Provides
    public AppCompatActivity provideActivity() {
        return activity;
    }

    @Named(Names.ACTIVITY_CONTEXT)
    @ActivityScope
    @Provides
    public Context provideContext() {
        return activity;
    }

    @ActivityScope
    @Provides
    public ContentResolver provideContentResolver() {
        return activity.getContentResolver();
    }

    @ActivityScope
    @Provides
    public TangoWrapper provideTangoWrapper(@Named(Names.ACTIVITY_CONTEXT) Context context) {
        TangoWrapper tangoWrapper = new TangoWrapper(context);
        tangoWrapper.setStartTangoUx(false);
        tangoWrapper.setCoordinateFramePairs(FRAME_PAIRS);
        return tangoWrapper;
    }
}
