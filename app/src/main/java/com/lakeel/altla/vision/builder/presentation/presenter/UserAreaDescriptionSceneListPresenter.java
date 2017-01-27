package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.presentation.mapper.UserAreaDescriptionSceneModelMapper;
import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionSceneModel;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionSceneItemView;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionSceneView;
import com.lakeel.altla.vision.domain.usecase.FindAllUserAreaDescriptionsSceneUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionSceneListPresenter {

    private static final Log LOG = LogFactory.getLog(UserAreaDescriptionSceneListPresenter.class);

    @Inject
    FindAllUserAreaDescriptionsSceneUseCase findAllUserAreaDescriptionsSceneUseCase;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final List<UserAreaDescriptionSceneModel> items = new ArrayList<>();

    private String areaDescriptionId;

    private UserAreaDescriptionSceneView view;

    @Inject
    public UserAreaDescriptionSceneListPresenter() {
    }

    public void onCreate(@NonNull String areaDescriptionId) {
        this.areaDescriptionId = areaDescriptionId;
    }

    public void onCreateView(@NonNull UserAreaDescriptionSceneView view) {
        this.view = view;
    }

    public void onStart() {
        items.clear();

        Disposable disposable = findAllUserAreaDescriptionsSceneUseCase
                .execute(areaDescriptionId)
                .map(UserAreaDescriptionSceneModelMapper::map)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(models -> {
                    items.addAll(models);
                    view.updateItems();
                }, e -> {
                    LOG.e("Failed to find all user scenes.");
                });
        compositeDisposable.add(disposable);
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    public void onCreateItemView(@NonNull UserAreaDescriptionSceneItemView itemView) {
        UserAreaDescriptionSceneListPresenter.ItemPresenter itemPresenter =
                new UserAreaDescriptionSceneListPresenter.ItemPresenter();
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
