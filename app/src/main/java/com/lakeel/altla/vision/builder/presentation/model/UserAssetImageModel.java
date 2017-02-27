package com.lakeel.altla.vision.builder.presentation.model;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class UserAssetImageModel {

    private static final String EXTRA_USER_ID = "userId";

    private static final String EXTRA_ASSET_ID = "assetId";

    private static final String EXTRA_URI = "uri";

    public String userId;

    public String assetId;

    public Uri uri;

    public Intent createIntent() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_ASSET_ID, assetId);
        intent.putExtra(EXTRA_URI, uri);
        return intent;
    }

    @Nullable
    public static UserAssetImageModel parseIntent(@NonNull Intent intent) {
        UserAssetImageModel model = new UserAssetImageModel();
        model.userId = intent.getStringExtra(EXTRA_USER_ID);
        model.assetId = intent.getStringExtra(EXTRA_ASSET_ID);
        model.uri = intent.getParcelableExtra(EXTRA_URI);
        return model;
    }
}
