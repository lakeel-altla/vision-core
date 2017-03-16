package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.model.ImageAsset;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface UserImageAssetItemView {

    void onUpdateName(@Nullable String name);

    void onUpdateThumbnail(@NonNull Uri uri);

    void onStartDrag(@NonNull ImageAsset asset);
}
