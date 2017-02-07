package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.presentation.mapper.UserAreaDescriptionModelMapper;
import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionModel;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionView;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaDescriptionUseCase;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionPresenter {

    private static final Log LOG = LogFactory.getLog(UserAreaDescriptionPresenter.class);

    @Inject
    FindUserAreaDescriptionUseCase findUserAreaDescriptionUseCase;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private String areaDescriptionId;

    private UserAreaDescriptionView view;

    private UserAreaDescriptionModel model;

    @Inject
    public UserAreaDescriptionPresenter() {
    }

    public void onCreate(@NonNull String areaDescriptionId) {
        this.areaDescriptionId = areaDescriptionId;
    }

    public void onCreateView(@NonNull UserAreaDescriptionView view) {
        this.view = view;
    }

    public void onStart() {
        Disposable disposable = findUserAreaDescriptionUseCase
                .execute(areaDescriptionId)
                .map(UserAreaDescriptionModelMapper::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    this.model = model;
                    view.showModel(model);
                }, e -> {
                    LOG.e(String.format("Failed to find the user area description: areaDescriptionId = %s",
                                        areaDescriptionId), e);
                }, () -> {
                    throw new IllegalStateException(
                            String.format("The user area description not found: areaDescriptionId = %s",
                                          areaDescriptionId));
                });
        compositeDisposable.add(disposable);
    }

    public void onStop() {
        compositeDisposable.clear();
    }
}
