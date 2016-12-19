package com.lakeel.altla.vision.domain.repository;

import java.io.File;

import rx.Observable;
import rx.Single;

public interface TextureCacheRepository {

    Observable<File> find(String textureId);

    Single<File> create(String textureId);

    Single<String> delete(String textureId);
}
