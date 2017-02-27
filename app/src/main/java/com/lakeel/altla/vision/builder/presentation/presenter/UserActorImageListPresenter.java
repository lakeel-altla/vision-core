package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.UserActorImageModel;
import com.lakeel.altla.vision.builder.presentation.view.UserActorImageItemView;
import com.lakeel.altla.vision.builder.presentation.view.UserActorImageListView;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.model.UserActorImage;
import com.lakeel.altla.vision.domain.usecase.GetUserActorImageFileUriUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveAllUserActorImageUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserActorImageListPresenter extends BasePresenter<UserActorImageListView>
        implements DataList.OnItemListener {

    private final DataList<ItemModel> items = new DataList<>(this);

    @Inject
    ObserveAllUserActorImageUseCase observeAllUserActorImageUseCase;

    @Inject
    GetUserActorImageFileUriUseCase getUserActorImageFileUriUseCase;

    private Disposable getUserActorImageFileUriUseCaseDisposable;

    @Inject
    public UserActorImageListPresenter() {
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateImageButtonExpandVisible(true);
        getView().onUpdateImageButtonCollapseVisible(false);
        getView().onUpdateContentVisible(false);
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();

        Disposable disposable = observeAllUserActorImageUseCase
                .execute()
                .map(this::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.change(model.type, model.item, model.previousAreaId);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    @Override
    protected void onPauseOverride() {
        super.onPauseOverride();

        cancelStartDrag();
    }

    @Override
    public void onItemInserted(int index) {
        getView().onItemInserted(index);
    }

    @Override
    public void onItemChanged(int index) {
        getView().onItemChanged(index);
    }

    @Override
    public void onItemRemoved(int index) {
        getView().onItemRemoved(index);
    }

    @Override
    public void onItemMoved(int from, int to) {
        getView().onItemMoved(from, to);
    }

    @Override
    public void onDataSetChanged() {
        getView().onDataSetChanged();
    }

    public int getItemCount() {
        return items.size();
    }

    @NonNull
    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public void onClickImageButtonExpand() {
        getView().onUpdateImageButtonExpandVisible(false);
        getView().onUpdateImageButtonCollapseVisible(true);
        getView().onUpdateContentVisible(true);
    }

    public void onClickImageButtonCollapse() {
        getView().onUpdateImageButtonExpandVisible(true);
        getView().onUpdateImageButtonCollapseVisible(false);
        getView().onUpdateContentVisible(false);
    }

    @NonNull
    private EventModel map(@NonNull DataListEvent<UserActorImage> event) {
        EventModel model = new EventModel();
        model.type = event.getType();
        model.item = map(event.getData());
        model.previousAreaId = event.getPreviousChildName();
        return model;
    }

    @NonNull
    private ItemModel map(@NonNull UserActorImage userActorImage) {
        ItemModel model = new ItemModel();
        model.userId = userActorImage.userId;
        model.imageId = userActorImage.imageId;
        model.name = userActorImage.name;
        return model;
    }

    public final class ItemPresenter {

        private UserActorImageItemView itemView;

        public void onCreateItemView(@NonNull UserActorImageItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            ItemModel model = items.get(position);
            itemView.onUpdateName(model.name);

            Disposable disposable = getUserActorImageFileUriUseCase
                    .execute(model.imageId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(uri -> {
                        itemView.onUpdateThumbnail(uri);
                    }, e -> {
                        getLog().e("Failed.", e);
                    });
            manageDisposable(disposable);
        }

        public void onLongClickViewTop(int position) {
            cancelStartDrag();

            ItemModel model = items.get(position);

            getUserActorImageFileUriUseCaseDisposable = getUserActorImageFileUriUseCase
                    .execute(model.imageId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(uri -> {
                        UserActorImageModel userActorImageModel = new UserActorImageModel();
                        userActorImageModel.userId = model.userId;
                        userActorImageModel.imageId = model.imageId;
                        userActorImageModel.uri = uri;
                        itemView.onStartDrag(userActorImageModel);
                    }, e -> {
                        getLog().e("Failed.", e);
                    });
        }
    }

    private void cancelStartDrag() {
        if (getUserActorImageFileUriUseCaseDisposable != null) {
            getUserActorImageFileUriUseCaseDisposable.dispose();
            getUserActorImageFileUriUseCaseDisposable = null;
        }
    }

    private final class EventModel {

        DataListEvent.Type type;

        String previousAreaId;

        ItemModel item;
    }

    private final class ItemModel implements DataList.Item {

        String userId;

        String imageId;

        String name;

        @Override
        public String getId() {
            return imageId;
        }
    }
}
