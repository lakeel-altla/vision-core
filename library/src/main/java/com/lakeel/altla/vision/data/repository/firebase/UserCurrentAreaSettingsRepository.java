package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.model.CurrentAreaSettings;

import android.support.annotation.NonNull;

public final class UserCurrentAreaSettingsRepository extends BaseDatabaseRepository {

    private static final String BASE_PATH = "userCurrentAreaSettings";

    public UserCurrentAreaSettingsRepository(@NonNull FirebaseDatabase database) {
        super(database);
    }

    public void save(@NonNull CurrentAreaSettings currentAreaSettings) {
        if (currentAreaSettings.getUserId() == null) {
            throw new IllegalArgumentException("currentAreaSettings.getUserId() must be not null.");
        }

        currentAreaSettings.setUpdatedAtAsLong(-1);

        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(currentAreaSettings.getUserId())
                     .child(currentAreaSettings.getId())
                     .setValue(currentAreaSettings, (error, reference) -> {
                         if (error != null) {
                             getLog().e(String.format("Failed to save: reference = %s", reference),
                                        error.toException());
                         }
                     });
    }

    public void find(@NonNull String userId, @NonNull String instanceId,
                     OnSuccessListener<CurrentAreaSettings> onSuccessListener, OnFailureListener onFailureListener) {
        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(instanceId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             CurrentAreaSettings currentAreaSettings = snapshot.getValue(CurrentAreaSettings.class);
                             if (onSuccessListener != null) onSuccessListener.onSuccess(currentAreaSettings);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }
}
