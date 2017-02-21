package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.UserSceneCreateView;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.UserScene;
import com.lakeel.altla.vision.domain.usecase.SaveUserSceneUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class UserSceneCreatePresenter extends BasePresenter<UserSceneCreateView> {

    private static final String ARG_AREA_ID = "areaId";

    @Inject
    SaveUserSceneUseCase saveUserSceneUseCase;

    @Inject
    CurrentUserResolver currentUserResolver;

    private String areaId;

    private String name;

    private boolean processing;

    @Inject
    public UserSceneCreatePresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull String areaId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AREA_ID, areaId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        String areaId = arguments.getString(ARG_AREA_ID, null);
        if (areaId == null) {
            throw new IllegalStateException(String.format("Argument '%s' must be not null.", ARG_AREA_ID));
        }

        this.areaId = areaId;
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateButtonCreateEnabled(canCreate());
    }

    public void onEditTextNameAfterTextChanged(String value) {
        name = value;

        getView().onUpdateButtonCreateEnabled(canCreate());
    }

    public void onClickButtonCreate() {
        if (processing) return;

        processing = true;

        String userId = currentUserResolver.getUserId();
        String sceneId = UUID.randomUUID().toString();

        UserScene userScene = new UserScene(userId, sceneId);
        userScene.areaId = areaId;
        userScene.name = name;
        userScene.createdAt = -1;
        userScene.updatedAt = -1;

        Disposable disposable = saveUserSceneUseCase
                .execute(userScene)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> processing = false)
                .subscribe(() -> {
                    getView().onCreated();
                }, e -> {
                    getLog().e(String.format("Failed: areaId = %s", areaId), e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    private boolean canCreate() {
        return name != null;
    }
}
