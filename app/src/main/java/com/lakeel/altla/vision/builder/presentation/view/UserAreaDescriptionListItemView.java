package com.lakeel.altla.vision.builder.presentation.view;

import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionModel;
import com.lakeel.altla.vision.builder.presentation.presenter.UserAreaDescriptionListPresenter;

import android.support.annotation.NonNull;

public interface UserAreaDescriptionListItemView {

    void setItemPresenter(@NonNull UserAreaDescriptionListPresenter.ItemPresenter itemPresenter);

    void showModel(@NonNull UserAreaDescriptionModel model);
}
