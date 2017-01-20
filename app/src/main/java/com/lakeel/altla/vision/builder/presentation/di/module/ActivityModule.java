package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.atap.tangoservice.TangoConfig;
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
        tangoWrapper.setStartTangoUx(true);
        tangoWrapper.setCoordinateFramePairs(FRAME_PAIRS);
        tangoWrapper.setTangoConfigFactory(tango -> {
            TangoConfig config = tango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);

            // NOTE:
            // Low latency integration is necessary to achieve a precise alignment of
            // virtual objects with the RBG image and produce a good AR effect.
            config.putBoolean(TangoConfig.KEY_BOOLEAN_LOWLATENCYIMUINTEGRATION, true);
            // Enable the depth perseption.
            config.putBoolean(TangoConfig.KEY_BOOLEAN_DEPTH, true);
            config.putInt(TangoConfig.KEY_INT_DEPTH_MODE, TangoConfig.TANGO_DEPTH_MODE_POINT_CLOUD);
            // Enable the color camera.
            config.putBoolean(TangoConfig.KEY_BOOLEAN_COLORCAMERA, true);
            // NOTE:
            // To detect recovery from tracking lost, enable drift collection.
            //
            // Corrected drift pose data is available in frame pairs for any target frame
            // from base frame TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION.
            // In the official sample java_plane_fitting_example,
            // it is assumed that the target frame is TangoPoseData.COORDINATE_FRAME_DEVICE in the comment sentence,
            // but it can also be used as TangoPoseData.COORDINATE_FRAME_CAMERA_COLOR as in the same sample.
            //
            // Note that frame pairs based on COORDINATE_FRAME_AREA_DESCRIPTION do not work
            // unless drift collection is enabled.
            // Although it seems to work if you enable motion tracking,
            // setting KEY_BOOLEAN_MOTIONTRACKING to true does not work.
            config.putBoolean(TangoConfig.KEY_BOOLEAN_DRIFT_CORRECTION, true);

            return config;
        });
        return tangoWrapper;
    }
}
