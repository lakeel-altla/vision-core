package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaItemView;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaListView;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.model.Area;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveAllUserAreasUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class UserAreaListPresenter extends BasePresenter<UserAreaListView>
        implements DataList.OnItemListener {

    private final DataList<Item> items = new DataList<>(this);

    @Inject
    ObserveAllUserAreasUseCase findAllUserAreasUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    @Inject
    public UserAreaListPresenter() {
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();

        Disposable disposable = findAllUserAreasUseCase
                .execute()
                .map(Event::new)
                .concatMap(model -> {
                    if (model.item.area.getPlaceId() == null) {
                        return Observable.just(model);
                    } else {
                        return getPlaceUseCase
                                .execute(model.item.area.getPlaceId())
                                .map(place -> {
                                    model.item.placeName = place.getName().toString();
                                    model.item.placeAddress = place.getAddress().toString();
                                    return model;
                                })
                                .toObservable();
                    }
                })
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

    public void onClickItem(int position) {
        Item item = items.get(position);
        getView().onItemSelected(item.area.getId());
    }

    public final class ItemPresenter {

        private UserAreaItemView itemView;

        public void onCreateItemView(@NonNull UserAreaItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            Item item = items.get(position);
            itemView.onUpdateAreaId(item.area.getId());
            itemView.onUpdateName(item.area.getName());
            itemView.onUpdatePlaceName(item.placeName);
            itemView.onUpdatePladeAddress(item.placeAddress);
            itemView.onUpdateLevel(item.area.getLevel());
        }
    }

    private final class Event {

        final DataListEvent.Type type;

        final Item item;

        final String previousId;

        Event(@NonNull DataListEvent<Area> event) {
            type = event.getType();
            item = new Item(event.getData());
            previousId = event.getPreviousChildName();
        }
    }

    private final class Item implements DataList.Item {

        final Area area;

        String placeName;

        String placeAddress;

        Item(@NonNull Area area) {
            this.area = area;
        }

        @Override
        public String getId() {
            return area.getId();
        }
    }
}
