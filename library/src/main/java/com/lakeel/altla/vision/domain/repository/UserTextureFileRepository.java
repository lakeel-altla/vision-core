package com.lakeel.altla.vision.domain.repository;

import java.io.File;
import java.io.InputStream;

import rx.Single;

public interface UserTextureFileRepository {

    Single<String> save(String textureId, InputStream stream, OnProgressListener onProgressListener);

    Single<String> delete(String textureId);

    Single<String> download(String textureId, File destination, OnProgressListener onProgressListener);

    interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }
}
