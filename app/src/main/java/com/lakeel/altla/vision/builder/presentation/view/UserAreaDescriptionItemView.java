package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionModel;
import com.lakeel.altla.vision.builder.presentation.presenter.UserAreaDescriptionsPresenter;

import android.support.annotation.NonNull;

public interface UserAreaDescriptionItemView {

    void setItemPresenter(@NonNull UserAreaDescriptionsPresenter.ItemPresenter itemPresenter);

    void showModel(@NonNull UserAreaDescriptionModel model);
}
