package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.builder.presentation.mapper.UserAreaDescriptionModelMapper;
import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionItemModel;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionItemView;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionListView;
import com.lakeel.altla.vision.domain.usecase.FindAllUserAreaDescriptionsUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionListPresenter extends BasePresenter<UserAreaDescriptionListView> {

    @Inject
    FindAllUserAreaDescriptionsUseCase findAllUserAreaDescriptionsUseCase;

    private final List<UserAreaDescriptionItemModel> items = new ArrayList<>();

    @Inject
    public UserAreaDescriptionListPresenter() {
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();

        Disposable disposable = findAllUserAreaDescriptionsUseCase
                .execute()
                .map(UserAreaDescriptionModelMapper::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.add(model);
                    getView().onItemInserted(items.size() - 1);
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable);
    }

    public int getItemCount() {
        return items.size();
    }

    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public final class ItemPresenter {

        private UserAreaDescriptionItemView itemView;

        public void onCreateItemView(@NonNull UserAreaDescriptionItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            UserAreaDescriptionItemModel model = items.get(position);
            itemView.onModelUpdated(model);
        }

        public void onClick(int position) {
            UserAreaDescriptionItemModel model = items.get(position);
            getView().onShowUserAreaDescriptionScenesView(model.areaDescriptionId);
        }
    }
}
