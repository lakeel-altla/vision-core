package com.lakeel.altla.vision.domain.repository;

import java.io.File;
import java.io.InputStream;

import rx.Single;

public interface UserAreaDescriptionFileRepository {

    Single<String> upload(String areaDescriptionId, InputStream stream, OnProgressListener onProgressListener);

    Single<String> download(String areaDescriptionId, File destination, OnProgressListener onProgressListener);

    Single<String> delete(String areaDescriptionId);

    interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }
}
