package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.UserImageAssetItemView;
import com.lakeel.altla.vision.builder.presentation.view.UserImageAssetListView;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.model.ImageAsset;
import com.lakeel.altla.vision.domain.usecase.GetUserImageAssetFileUriUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveAllUserImageAssetUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserImageAssetListPresenter extends BasePresenter<UserImageAssetListView>
        implements DataList.OnItemListener {

    private final DataList<Item> items = new DataList<>(this);

    @Inject
    ObserveAllUserImageAssetUseCase observeAllUserImageAssetUseCase;

    @Inject
    GetUserImageAssetFileUriUseCase getUserImageAssetFileUriUseCase;

    private Disposable getUserImageAssetFileUriUseCaseDisposable;

    @Inject
    public UserImageAssetListPresenter() {
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

        Disposable disposable = observeAllUserImageAssetUseCase
                .execute()
                .map(Event::new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    items.change(event.type, event.item, event.previousId);
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

    public final class ItemPresenter {

        private UserImageAssetItemView itemView;

        public void onCreateItemView(@NonNull UserImageAssetItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            Item item = items.get(position);
            itemView.onUpdateName(item.asset.getName());

            Disposable disposable = getUserImageAssetFileUriUseCase
                    .execute(item.asset.getId())
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

            Item item = items.get(position);

            itemView.onStartDrag(item.asset);
        }
    }

    private void cancelStartDrag() {
        if (getUserImageAssetFileUriUseCaseDisposable != null) {
            getUserImageAssetFileUriUseCaseDisposable.dispose();
            getUserImageAssetFileUriUseCaseDisposable = null;
        }
    }

    private final class Event {

        final DataListEvent.Type type;

        final Item item;

        final String previousId;

        Event(@NonNull DataListEvent<ImageAsset> event) {
            type = event.getType();
            item = new Item(event.getData());
            previousId = event.getPreviousChildName();
        }
    }

    private final class Item implements DataList.Item {

        final ImageAsset asset;

        Item(@NonNull ImageAsset asset) {
            this.asset = asset;
        }

        @Override
        public String getId() {
            return asset.getId();
        }
    }
}
