package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.storage.FirebaseStorage;

import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.model.TextureFileMetadata;

import android.support.annotation.NonNull;

public final class UserTextureFileMetadataRepository extends BaseStorageRepository {

    private static final String PATH_USER_TEXTURES = "userTextures";

    public UserTextureFileMetadataRepository(@NonNull FirebaseStorage storage) {
        super(storage);
    }

    public void find(@NonNull String userId, @NonNull String textureId,
                     OnSuccessListener<TextureFileMetadata> onSuccessListener, OnFailureListener onFailureListener) {
        getStorage().getReference()
                    .child(PATH_USER_TEXTURES)
                    .child(userId)
                    .child(textureId)
                    .getMetadata()
                    .addOnSuccessListener(storageMetadata -> {
                        TextureFileMetadata fileMetadata = new TextureFileMetadata();
                        fileMetadata.createTimeMillis = storageMetadata.getCreationTimeMillis();
                        fileMetadata.updateTimeMillis = storageMetadata.getUpdatedTimeMillis();
                        if (onSuccessListener != null) onSuccessListener.onSuccess(fileMetadata);
                    })
                    .addOnFailureListener(e -> {
                        if (onFailureListener != null) onFailureListener.onFailure(e);
                    });
    }
}
