package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionSceneModel;
import com.lakeel.altla.vision.builder.presentation.presenter.UserAreaDescriptionScenesPresenter;

import android.support.annotation.NonNull;

public interface UserAreaDescriptionSceneItemView {

    void setItemPresenter(@NonNull UserAreaDescriptionScenesPresenter.ItemPresenter itemPresenter);

    void showModel(@NonNull UserAreaDescriptionSceneModel model);
}
