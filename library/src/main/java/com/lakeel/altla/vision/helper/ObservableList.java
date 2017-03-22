package com.lakeel.altla.vision.helper;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;

import android.support.annotation.NonNull;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

public final class ObservableList<TData> implements Closeable {

    private static final Log LOG = LogFactory.getLog(ObservableList.class);

    private final Query query;

    private final DataSnapshotConverter<TData> dataSnapshotConverter;

    private final List<OnObservableListEventListener<TData>> onObservableListEventListeners = new ArrayList<>();

    private final List<OnFailureListener> onFailureListeners = new ArrayList<>();

    private ChildEventListener childEventListener;

    private boolean closed;

    public ObservableList(@NonNull Query query, @NonNull DataSnapshotConverter<TData> dataSnapshotConverter) {
        this.query = query;
        this.dataSnapshotConverter = dataSnapshotConverter;
    }

    public void addOnDataListEventListener(@NonNull OnObservableListEventListener<TData> listener) {
        onObservableListEventListeners.add(listener);
    }

    public void removeOnDataListEventListener(
            @NonNull OnObservableListEventListener<TData> listener) {
        onObservableListEventListeners.remove(listener);
    }

    public void addOnFailureListener(@NonNull OnFailureListener listener) {
        onFailureListeners.add(listener);
    }

    public void removeOnFailureListener(@NonNull OnFailureListener listener) {
        onFailureListeners.remove(listener);
    }

    public void observe() {
        if (closed) throw new IllegalStateException("This object is closed.");

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                TData data = dataSnapshotConverter.convert(snapshot);
                for (OnObservableListEventListener<TData> listener : onObservableListEventListeners) {
                    listener.onObservableListEvent(
                            new ObservableListEvent<>(ObservableListEvent.Type.ADDED, data, previousChildName));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                TData data = dataSnapshotConverter.convert(snapshot);
                for (OnObservableListEventListener<TData> listener : onObservableListEventListeners) {
                    listener.onObservableListEvent(
                            new ObservableListEvent<>(ObservableListEvent.Type.CHANGED, data, previousChildName));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                TData data = dataSnapshotConverter.convert(snapshot);
                for (OnObservableListEventListener<TData> listener : onObservableListEventListeners) {
                    listener.onObservableListEvent(
                            new ObservableListEvent<>(ObservableListEvent.Type.REMOVED, data, null));
                }
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                TData data = dataSnapshotConverter.convert(snapshot);
                for (OnObservableListEventListener<TData> listener : onObservableListEventListeners) {
                    listener.onObservableListEvent(
                            new ObservableListEvent<>(ObservableListEvent.Type.MOVED, data, previousChildName));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Exception e = error.toException();
                for (OnFailureListener listener : onFailureListeners) {
                    listener.onFailure(e);
                }
            }
        };
        query.addChildEventListener(childEventListener);
    }

    @Override
    public void close() {
        if (childEventListener != null) {
            query.removeEventListener(childEventListener);
            childEventListener = null;
        }

        closed = true;

        LOG.v("Closed.");
    }

    public interface DataSnapshotConverter<TData> {

        TData convert(DataSnapshot snapshot);
    }
}
