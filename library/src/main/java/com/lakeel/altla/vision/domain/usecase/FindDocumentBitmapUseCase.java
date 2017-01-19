package com.lakeel.altla.vision.domain.usecase;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.DocumentBitmapRepository;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.IOException;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class FindDocumentBitmapUseCase {

    @Inject
    DocumentBitmapRepository documentBitmapRepository;

    @Inject
    public FindDocumentBitmapUseCase() {
    }

    public Single<Bitmap> execute(Uri uri) {
        if (uri == null) throw new ArgumentNullException("uri");

        return Single.<Bitmap>create(subscriber -> {
            try {
                Bitmap bitmap = documentBitmapRepository.find(uri);
                subscriber.onSuccess(bitmap);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }
}
