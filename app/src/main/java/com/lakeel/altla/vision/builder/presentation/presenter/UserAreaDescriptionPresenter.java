package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.presentation.mapper.UserAreaDescriptionModelMapper;
import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionItemModel;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionView;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionPresenter extends BasePresenter<UserAreaDescriptionView> {

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    @Inject
    FindUserAreaDescriptionUseCase findUserAreaDescriptionUseCase;

    private String areaDescriptionId;

    private UserAreaDescriptionItemModel model;

    @Inject
    public UserAreaDescriptionPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull String areaDescriptionId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AREA_DESCRIPTION_ID, areaDescriptionId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        String areaDescriptionId = arguments.getString(ARG_AREA_DESCRIPTION_ID);
        if (areaDescriptionId == null) {
            throw new IllegalStateException(String.format("Argument '%s' must be not null", ARG_AREA_DESCRIPTION_ID));
        }

        this.areaDescriptionId = areaDescriptionId;
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        Disposable disposable = findUserAreaDescriptionUseCase
                .execute(areaDescriptionId)
                .map(UserAreaDescriptionModelMapper::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    this.model = model;
                    getView().onModelUpdated(model);
                }, e -> {
                    getLog().e(String.format("Failed to find the user area description: areaDescriptionId = %s",
                                             areaDescriptionId), e);
                }, () -> {
                    throw new IllegalStateException(
                            String.format("The user area description not found: areaDescriptionId = %s",
                                          areaDescriptionId));
                });
        manageDisposable(disposable);
    }
}
