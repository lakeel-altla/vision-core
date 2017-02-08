package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.presentation.mapper.UserAreaDescriptionSceneItemModelMapper;
import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionSceneItemModel;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionSceneItemView;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionSceneListView;
import com.lakeel.altla.vision.domain.usecase.FindAllUserAreaDescriptionsSceneUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionSceneListPresenter extends BasePresenter<UserAreaDescriptionSceneListView> {

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    @Inject
    FindAllUserAreaDescriptionsSceneUseCase findAllUserAreaDescriptionsSceneUseCase;

    private final List<UserAreaDescriptionSceneItemModel> items = new ArrayList<>();

    private String areaDescriptionId;

    @Inject
    public UserAreaDescriptionSceneListPresenter() {
    }

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
            throw new IllegalStateException(String.format("Argument '%s' must be not null.", ARG_AREA_DESCRIPTION_ID));
        }

        this.areaDescriptionId = areaDescriptionId;
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();

        Disposable disposable = findAllUserAreaDescriptionsSceneUseCase
                .execute(areaDescriptionId)
                .map(UserAreaDescriptionSceneItemModelMapper::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.add(model);
                    getView().onItemInserted(items.size() - 1);
                }, e -> {
                    getLog().e("Failed to find all user scenes.", e);
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

    public final class ItemPresenter {

        private UserAreaDescriptionSceneItemView itemView;

        public void onCreateItemView(@NonNull UserAreaDescriptionSceneItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            UserAreaDescriptionSceneItemModel model = items.get(position);
            itemView.onModelUpdated(model);
        }
    }
}
