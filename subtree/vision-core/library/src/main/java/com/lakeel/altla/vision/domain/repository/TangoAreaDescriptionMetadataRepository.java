package com.lakeel.altla.vision.domain.repository;

import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import rx.Observable;
import rx.Single;

public interface TangoAreaDescriptionMetadataRepository {

    Observable<TangoAreaDescriptionMetaData> find(String areaDescriptionId);

    Observable<TangoAreaDescriptionMetaData> findAll();

    Single<String> delete(String areaDescriptionId);
}
