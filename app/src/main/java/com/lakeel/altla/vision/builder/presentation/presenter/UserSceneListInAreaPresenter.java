package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.presentation.mapper.UserSceneItemModelMapper;
import com.lakeel.altla.vision.builder.presentation.model.UserSceneItemModel;
import com.lakeel.altla.vision.builder.presentation.view.UserSceneItemView;
import com.lakeel.altla.vision.builder.presentation.view.UserSceneListInAreaView;
import com.lakeel.altla.vision.domain.usecase.FindUserScenesByAreaIdUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserSceneListInAreaPresenter extends BasePresenter<UserSceneListInAreaView> {

    private static final String ARG_AREA_ID = "areaId";

    @Inject
    FindUserScenesByAreaIdUseCase findUserScenesByAreaIdUseCase;

    private final List<UserSceneItemModel> items = new ArrayList<>();

    private String areaId;

    @Inject
    public UserSceneListInAreaPresenter() {
    }

    public static Bundle createArguments(@NonNull String areaId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AREA_ID, areaId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        String areaId = arguments.getString(ARG_AREA_ID);
        if (areaId == null) {
            throw new IllegalStateException(String.format("Argument '%s' must be not null.", ARG_AREA_ID));
        }

        this.areaId = areaId;
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();

        Disposable disposable = findUserScenesByAreaIdUseCase
                .execute(areaId)
                .map(UserSceneItemModelMapper::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.add(model);
                    getView().onItemInserted(items.size() - 1);
                }, e -> {
                    getLog().e(String.format("Failed: areaId = %s", areaId), e);
                });
        manageDisposable(disposable);
    }

    public int getItemCount() {
        return items.size();
    }

    @NonNull
    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public void onActionCreate() {
        getView().onShowUserSceneCreateView();
    }

    public final class ItemPresenter {

        private UserSceneItemView itemView;

        public void onCreateItemView(@NonNull UserSceneItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            UserSceneItemModel model = items.get(position);
            itemView.onModelUpdated(model);
        }
    }
}
