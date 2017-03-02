package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.UserAssetImageDragModel;
import com.lakeel.altla.vision.builder.presentation.view.UserAssetImageItemView;
import com.lakeel.altla.vision.builder.presentation.view.UserAssetImageListView;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.model.UserAssetImage;
import com.lakeel.altla.vision.domain.usecase.GetUserAssetImageFileUriUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveAllUserAssetImageUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAssetImageListPresenter extends BasePresenter<UserAssetImageListView>
        implements DataList.OnItemListener {

    private final DataList<ItemModel> items = new DataList<>(this);

    @Inject
    ObserveAllUserAssetImageUseCase observeAllUserAssetImageUseCase;

    @Inject
    GetUserAssetImageFileUriUseCase getUserAssetImageFileUriUseCase;

    private Disposable getUserAssetImageFileUriUseCaseDisposable;

    @Inject
    public UserAssetImageListPresenter() {
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

        Disposable disposable = observeAllUserAssetImageUseCase
                .execute()
                .map(this::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.change(model.type, model.item, model.previousId);
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
    private EventModel map(@NonNull DataListEvent<UserAssetImage> event) {
        EventModel model = new EventModel();
        model.type = event.getType();
        model.item = map(event.getData());
        model.previousId = event.getPreviousChildName();
        return model;
    }

    @NonNull
    private ItemModel map(@NonNull UserAssetImage userAssetImage) {
        ItemModel model = new ItemModel();
        model.userId = userAssetImage.userId;
        model.assetId = userAssetImage.assetId;
        model.name = userAssetImage.name;
        return model;
    }

    public final class ItemPresenter {

        private UserAssetImageItemView itemView;

        public void onCreateItemView(@NonNull UserAssetImageItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            ItemModel model = items.get(position);
            itemView.onUpdateName(model.name);

            Disposable disposable = getUserAssetImageFileUriUseCase
                    .execute(model.assetId)
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

            getUserAssetImageFileUriUseCaseDisposable = getUserAssetImageFileUriUseCase
                    .execute(model.assetId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(uri -> {
                        UserAssetImageDragModel userAssetImageDragModel = new UserAssetImageDragModel();
                        userAssetImageDragModel.userId = model.userId;
                        userAssetImageDragModel.assetId = model.assetId;
                        userAssetImageDragModel.name = model.name;
                        userAssetImageDragModel.uri = uri;
                        itemView.onStartDrag(userAssetImageDragModel);
                    }, e -> {
                        getLog().e("Failed.", e);
                    });
        }
    }

    private void cancelStartDrag() {
        if (getUserAssetImageFileUriUseCaseDisposable != null) {
            getUserAssetImageFileUriUseCaseDisposable.dispose();
            getUserAssetImageFileUriUseCaseDisposable = null;
        }
    }

    private final class EventModel {

        DataListEvent.Type type;

        String previousId;

        ItemModel item;
    }

    private final class ItemModel implements DataList.Item {

        String userId;

        String assetId;

        String name;

        @Override
        public String getId() {
            return assetId;
        }
    }
}
