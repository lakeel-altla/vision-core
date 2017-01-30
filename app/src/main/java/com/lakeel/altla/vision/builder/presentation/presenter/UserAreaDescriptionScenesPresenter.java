package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.presentation.mapper.UserAreaDescriptionSceneModelMapper;
import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionSceneModel;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionSceneItemView;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionScenesView;
import com.lakeel.altla.vision.domain.usecase.FindAllUserAreaDescriptionsSceneUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionScenesPresenter {

    private static final Log LOG = LogFactory.getLog(UserAreaDescriptionScenesPresenter.class);

    @Inject
    FindAllUserAreaDescriptionsSceneUseCase findAllUserAreaDescriptionsSceneUseCase;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final List<UserAreaDescriptionSceneModel> items = new ArrayList<>();

    private String areaDescriptionId;

    private UserAreaDescriptionScenesView view;

    @Inject
    public UserAreaDescriptionScenesPresenter() {
    }

    public void onCreate(@NonNull String areaDescriptionId) {
        this.areaDescriptionId = areaDescriptionId;
    }

    public void onCreateView(@NonNull UserAreaDescriptionScenesView view) {
        this.view = view;
    }

    public void onStart() {
        items.clear();

        Disposable disposable = findAllUserAreaDescriptionsSceneUseCase
                .execute(areaDescriptionId)
                .map(UserAreaDescriptionSceneModelMapper::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.add(model);
                    view.updateItem(items.size() - 1);
                }, e -> {
                    LOG.e("Failed to find all user scenes.", e);
                });
        compositeDisposable.add(disposable);
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    public void onCreateItemView(@NonNull UserAreaDescriptionSceneItemView itemView) {
        ItemPresenter itemPresenter = new ItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
    }

    public int getItemCount() {
        return items.size();
    }

    public final class ItemPresenter {

        private UserAreaDescriptionSceneItemView itemView;

        private void onCreateItemView(@NonNull UserAreaDescriptionSceneItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            UserAreaDescriptionSceneModel model = items.get(position);
            itemView.showModel(model);
        }
    }
}
