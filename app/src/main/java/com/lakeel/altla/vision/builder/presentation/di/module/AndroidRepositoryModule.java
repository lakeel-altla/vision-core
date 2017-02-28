package com.lakeel.altla.vision.builder.presentation.di.module;

import com.google.android.gms.common.api.GoogleApiClient;

import com.lakeel.altla.vision.data.repository.android.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.data.repository.android.DocumentBitmapRepository;
import com.lakeel.altla.vision.data.repository.android.DocumentFilenameRepository;
import com.lakeel.altla.vision.data.repository.android.DocumentRepository;
import com.lakeel.altla.vision.data.repository.android.FileBitmapRepository;
import com.lakeel.altla.vision.data.repository.android.PlaceRepository;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionMetadataRepository;
import com.lakeel.altla.vision.data.repository.android.TextureCacheRepository;
import com.lakeel.altla.vision.di.ActivityScope;

import android.content.ContentResolver;
import android.content.Context;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public final class AndroidRepositoryModule {

    @ActivityScope
    @Provides
    DocumentRepository provideDocumentRepository(ContentResolver contentResolver) {
        return new DocumentRepository(contentResolver);
    }

    @ActivityScope
    @Provides
    DocumentBitmapRepository provideDocumentBitmapRepository(ContentResolver contentResolver) {
        return new DocumentBitmapRepository(contentResolver);
    }

    @ActivityScope
    @Provides
    DocumentFilenameRepository provideDocumentFilenameRepository(ContentResolver contentResolver) {
        return new DocumentFilenameRepository(contentResolver);
    }

    @ActivityScope
    @Provides
    FileBitmapRepository provideFileBitmapRepository() {
        return new FileBitmapRepository();
    }

    @ActivityScope
    @Provides
    TextureCacheRepository provideTextureCacheRepository(@Named(Names.ACTIVITY_CONTEXT) Context context) {
        return new TextureCacheRepository(context);
    }

    @ActivityScope
    @Provides
    TangoAreaDescriptionMetadataRepository provideTangoMetadataRepository() {
        return new TangoAreaDescriptionMetadataRepository();
    }

    @ActivityScope
    @Provides
    AreaDescriptionCacheRepository provideAreaDescriptionCacheRepository(
            @Named(Names.EXTERNAL_STORAGE_ROOT) File rootDirectory) {
        return new AreaDescriptionCacheRepository(rootDirectory);
    }

    @ActivityScope
    @Provides
    PlaceRepository providePlaceRepository(GoogleApiClient googleApiClient) {
        return new PlaceRepository(googleApiClient);
    }
}
