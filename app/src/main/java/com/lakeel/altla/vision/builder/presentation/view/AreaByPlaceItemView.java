package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface AreaByPlaceItemView {

    void onUpdateAreaId(@NonNull String areaId);

    void onUpdateName(@Nullable String name);

    void onUpdateLevel(int level);
}
