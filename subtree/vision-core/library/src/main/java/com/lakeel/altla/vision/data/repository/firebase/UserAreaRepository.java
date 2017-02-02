package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.helper.OnFailureListener;
import com.lakeel.altla.vision.domain.helper.OnSuccessListener;
import com.lakeel.altla.vision.domain.model.UserArea;

import java.util.ArrayList;
import java.util.List;

public final class UserAreaRepository extends BaseDatabaseRepository {

    private static final Log LOG = LogFactory.getLog(UserAreaRepository.class);

    private static final String BASE_PATH = "userAreas";

    private static final String FIELD_NAME = "name";

    public UserAreaRepository(FirebaseDatabase database) {
        super(database);
    }

    public void save(UserArea userArea) {
        if (userArea == null) throw new ArgumentNullException("userArea");

        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userArea.userId)
                     .child(userArea.areaId)
                     .setValue(userArea, (error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to save: reference = %s", reference), error.toException());
                         }
                     });
    }

    public void find(String userId, String areaId, OnSuccessListener<UserArea> onSuccessListener,
                     OnFailureListener onFailureListener) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaId == null) throw new ArgumentNullException("areaId");

        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(areaId)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             UserArea userArea = null;
                             if (snapshot.exists()) {
                                 userArea = map(userId, snapshot);
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(userArea);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    public void findAll(String userId, OnSuccessListener<List<UserArea>> onSuccessListener,
                        OnFailureListener onFailureListener) {
        if (userId == null) throw new ArgumentNullException("userId");

        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .orderByChild(FIELD_NAME)
                     .addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot snapshot) {
                             List<UserArea> list = new ArrayList<>((int) snapshot.getChildrenCount());
                             for (DataSnapshot child : snapshot.getChildren()) {
                                 list.add(map(userId, child));
                             }
                             if (onSuccessListener != null) onSuccessListener.onSuccess(list);
                         }

                         @Override
                         public void onCancelled(DatabaseError error) {
                             if (onFailureListener != null) onFailureListener.onFailure(error.toException());
                         }
                     });
    }

    public void delete(String userId, String areaId) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (areaId == null) throw new ArgumentNullException("areaId");

        getDatabase().getReference()
                     .child(BASE_PATH)
                     .child(userId)
                     .child(areaId)
                     .removeValue((error, reference) -> {
                         if (error != null) {
                             LOG.e(String.format("Failed to remove: reference = %s", reference), error.toException());
                         }
                     });
    }

    private UserArea map(String userId, DataSnapshot snapshot) {
        UserArea userArea = snapshot.getValue(UserArea.class);
        userArea.userId = userId;
        userArea.areaId = snapshot.getKey();
        return userArea;
    }
}
