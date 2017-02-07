package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.EditTextureModel;
import com.lakeel.altla.vision.builder.presentation.view.RegisterTextureView;
import com.lakeel.altla.vision.domain.usecase.FindDocumentBitmapUseCase;
import com.lakeel.altla.vision.domain.usecase.FindDocumentFilenameUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserTextureBitmapUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserTextureUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserTextureUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class RegisterTexturePresenter extends BasePresenter<RegisterTextureView> {

    private static final String ARG_TEXTURE_ID = "textureId";

    @Inject
    FindUserTextureUseCase findUserTextureUseCase;

    @Inject
    FindUserTextureBitmapUseCase findUserTextureBitmapUseCase;

    @Inject
    FindDocumentBitmapUseCase findDocumentBitmapUseCase;

    @Inject
    FindDocumentFilenameUseCase findDocumentFilenameUseCase;

    @Inject
    SaveUserTextureUseCase saveUserTextureUseCase;

    private long prevBytesTransferred;

    private final EditTextureModel model = new EditTextureModel();

    private boolean localTextureSelected;

    @Inject
    public RegisterTexturePresenter() {
    }

    @NonNull
    public static Bundle createArguments(@Nullable String textureId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXTURE_ID, textureId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments != null) {
            model.textureId = arguments.getString(ARG_TEXTURE_ID, null);
        }
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTextureVisible(false);
        getView().onUpdateLoadTextureProgressVisible(false);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        getView().onModelUpdated(model);

        if (localTextureSelected) {
            localTextureSelected = false;

            if (model.name == null || model.name.length() == 0) {
                loadLocalTextureBitmapAndName();
            } else {
                loadLocalTextureBitmap();
            }
        } else if (model.textureId != null) {
            Disposable disposable = findUserTextureUseCase
                    // Find the texture entry to get its name.
                    .execute(model.textureId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(userTexture -> {
                        model.name = userTexture.name;
                        getView().onModelUpdated(model);

                        loadCachedTextureBitmap(userTexture.textureId);
                    }, e -> {
                        getLog().e(String.format("Failed: textureId = %s", model.textureId), e);
                    }, () -> {
                        getLog().e("The user texture not found: textureId = %s", model.textureId);
                    });
            manageDisposable(disposable);
        } else {
            getView().onUpdateTextureVisible(true);
        }
    }

    public void onClickButtonSelectDocument() {
        getView().onShowLocalTexturePicker();
    }

    public void onLocalTextureSelected(@NonNull Uri uri) {
        localTextureSelected = true;

        model.localUri = uri;

        // Release the bitmap if needed.
        if (model.bitmap != null) {
            model.bitmap.recycle();
            model.bitmap = null;
        }
    }

    private void loadCachedTextureBitmap(String textureId) {
        getView().onUpdateTextureVisible(false);

        Disposable disposable = findUserTextureBitmapUseCase
                .execute(textureId, null)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(_disposable -> getView().onUpdateLoadTextureProgressVisible(true))
                .doFinally(() -> getView().onUpdateLoadTextureProgressVisible(false))
                .subscribe(bitmap -> {
                    model.bitmap = bitmap;

                    getView().onUpdateTextureVisible(true);
                    getView().onModelUpdated(model);
                }, e -> {
                    // TODO: How to recover.
                    getLog().w(String.format("Failed to load the user texture bitmap: textureId = %s", textureId), e);
                });
        manageDisposable(disposable);
    }

    private void loadLocalTextureBitmap() {
        getView().onUpdateTextureVisible(false);

        Disposable disposable = findDocumentBitmapUseCase
                .execute(model.localUri)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(_disposable -> getView().onUpdateLoadTextureProgressVisible(true))
                .doFinally(() -> getView().onUpdateLoadTextureProgressVisible(false))
                .subscribe(bitmap -> {
                    model.bitmap = bitmap;

                    getView().onUpdateTextureVisible(true);
                    getView().onModelUpdated(model);
                }, e -> {
                    if (e instanceof FileNotFoundException) {
                        getLog().w(String.format("The image could not be found: uri = %s", model.localUri), e);
                        getView().onSnackbar(R.string.snackbar_image_file_not_found);
                    } else if (e instanceof IOException) {
                        getLog().w("Closing file failed.", e);
                    } else {
                        getLog().e("Unexpected error occured.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    }
                });
        manageDisposable(disposable);
    }

    private void loadLocalTextureBitmapAndName() {
        getView().onUpdateTextureVisible(false);

        Disposable disposable = Single
                .just(new LocalBitmap(model.localUri))
                // Load the bitmap.
                .flatMap(this::loadLocalBitmap)
                // Load the filename as a texture name if needed.
                .flatMap(this::loadLocalFilename)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(_disposable -> getView().onUpdateLoadTextureProgressVisible(true))
                .doFinally(() -> getView().onUpdateLoadTextureProgressVisible(false))
                .subscribe(localBitmap -> {
                    model.bitmap = localBitmap.bitmap;
                    model.name = localBitmap.name;

                    getView().onUpdateTextureVisible(true);
                    getView().onModelUpdated(model);
                }, e -> {
                    if (e instanceof FileNotFoundException) {
                        getLog().w(String.format("The image could not be found: uri = %s", model.localUri), e);
                        getView().onSnackbar(R.string.snackbar_image_file_not_found);
                    } else if (e instanceof IOException) {
                        getLog().w("Closing file failed.", e);
                    } else {
                        getLog().e("Unexpected error occured.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    }
                });
        manageDisposable(disposable);
    }

    private Single<LocalBitmap> loadLocalBitmap(LocalBitmap localBitmap) {
        return findDocumentBitmapUseCase.execute(localBitmap.uri)
                                        .map(bitmap -> {
                                            localBitmap.bitmap = bitmap;
                                            return localBitmap;
                                        });
    }

    private Single<LocalBitmap> loadLocalFilename(LocalBitmap localBitmap) {
        return findDocumentFilenameUseCase.execute(localBitmap.uri)
                                          .map(filename -> {
                                              localBitmap.name = filename;
                                              return localBitmap;
                                          });
    }

    public void onClickButtonRegister() {

        String localUri = (model.localUri != null) ? model.localUri.toString() : null;

        Disposable disposable = saveUserTextureUseCase
                .execute(model.textureId, model.name, localUri, (totalBytes, bytesTransferred) -> {
                    long increment = bytesTransferred - prevBytesTransferred;
                    prevBytesTransferred = bytesTransferred;

                    getView().onUpdateUploadProgressDialogProgress(totalBytes, increment);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(_disposable -> getView().onShowUploadProgressDialog())
                .doFinally(() -> getView().onHideUploadProgressDialog())
                .subscribe(textureId -> {
                    // Update ID of the model.
                    model.textureId = textureId;

                    getView().onSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e(String.format("Failed: textureId = %s", model.textureId), e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void afterNameChanged(String filename) {
        model.name = filename;
    }

    private final class LocalBitmap {

        private final Uri uri;

        private Bitmap bitmap;

        private String name;

        LocalBitmap(Uri uri) {
            this.uri = uri;
        }
    }
}
