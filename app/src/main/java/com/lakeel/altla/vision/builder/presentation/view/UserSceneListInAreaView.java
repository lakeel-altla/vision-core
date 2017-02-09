package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.NonNull;

public interface UserSceneListInAreaView {

    void onItemsUpdated();

    void onItemInserted(int position);

    void onItemSelected(@NonNull String sceneId);

    void onShowUserSceneCreateView(@NonNull String areaId);
}
