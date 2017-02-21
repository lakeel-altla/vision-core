package com.lakeel.altla.vision.builder.presentation.view;

import android.support.annotation.NonNull;

public interface UserSceneListInAreaView {

    void onItemInserted(int position);

    void onItemChanged(int position);

    void onItemRemoved(int position);

    void onItemMoved(int fromPosition, int toPosition);

    void onDataSetChanged();

    void onItemSelected(@NonNull String sceneId);

    void onShowUserSceneCreateView(@NonNull String areaId);
}
