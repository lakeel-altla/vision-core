package com.lakeel.altla.vision.builder.presentation.model;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class UserActorImageModel {

    private static final String EXTRA_USER_ID = "userId";

    private static final String EXTRA_IMAGE_ID = "imageId";

    private static final String EXTRA_URI = "uri";

    public String userId;

    public String imageId;

    public Uri uri;

    public Intent createIntent() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_IMAGE_ID, imageId);
        intent.putExtra(EXTRA_URI, uri);
        return intent;
    }

    @Nullable
    public static UserActorImageModel parseIntent(@NonNull Intent intent) {
        UserActorImageModel model = new UserActorImageModel();
        model.userId = intent.getStringExtra(EXTRA_USER_ID);
        model.imageId = intent.getStringExtra(EXTRA_IMAGE_ID);
        model.uri = intent.getParcelableExtra(EXTRA_URI);
        return model;
    }
}
