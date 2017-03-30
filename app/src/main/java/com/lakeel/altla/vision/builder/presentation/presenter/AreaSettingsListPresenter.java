package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsItemView;
import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsListView;
import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.model.AreaDescription;
import com.lakeel.altla.vision.model.Scope;
import com.lakeel.altla.vision.model.AreaSettings;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class AreaSettingsListPresenter extends BasePresenter<AreaSettingsListView> {

    @Inject
    VisionService visionService;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final List<Item> items = new ArrayList<>();

    private Item selectedItem;

    @Inject
    public AreaSettingsListPresenter() {
    }

    @Override
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateButtonSelectEnabled(canSelect());
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();
        getView().onDataSetChanged();

        Disposable disposable = Single
                .<List<AreaSettings>>create(e -> {
                    visionService.getUserAreaSettingsApi()
                                 .findAllUserAreaSettings(e::onSuccess, e::onError);
                })
                .flatMapObservable(Observable::fromIterable)
                .map(Item::new)
                .concatMap(item -> {
                    return Observable.<Item>create(e -> {
                        String areaId = item.areaSettings.getAreaId();
                        if (areaId == null) {
                            throw new IllegalStateException("Field 'areaId' is null.");
                        }

                        switch (item.areaSettings.getAreaScopeAsEnum()) {
                            case PUBLIC:
                                visionService.getPublicAreaApi()
                                             .findAreaById(areaId, area -> {
                                                 if (area != null) {
                                                     item.area = area;
                                                     e.onNext(item);
                                                 }
                                                 e.onComplete();
                                             }, e::onError);
                                break;
                            case USER:
                                visionService.getUserAreaApi()
                                             .findAreaById(areaId, area -> {
                                                 if (area != null) {
                                                     item.area = area;
                                                     e.onNext(item);
                                                 }
                                                 e.onComplete();
                                             }, e::onError);
                                break;
                            default:
                                throw new IllegalStateException("Unknown area scope.");
                        }
                    });
                })
                .concatMap(item -> {
                    return Observable.<Item>create(e -> {
                        String areaDescriptionId = item.areaSettings.getAreaDescriptionId();
                        if (areaDescriptionId == null) {
                            throw new IllegalStateException("Field 'areaId' is null.");
                        }

                        switch (item.areaSettings.getAreaScopeAsEnum()) {
                            case PUBLIC:
                                visionService.getPublicAreaDescriptionApi()
                                             .findAreaDescriptionById(areaDescriptionId, areaDescription -> {
                                                 if (areaDescription != null) {
                                                     item.areaDescription = areaDescription;
                                                     e.onNext(item);
                                                 }
                                                 e.onComplete();
                                             }, e::onError);
                                break;
                            case USER:
                                visionService.getUserAreaDescriptionApi()
                                             .findAreaDescriptionById(areaDescriptionId, areaDescription -> {
                                                 if (areaDescription != null) {
                                                     item.areaDescription = areaDescription;
                                                     e.onNext(item);
                                                 }
                                                 e.onComplete();
                                             }, e::onError);
                                break;
                            default:
                                throw new IllegalStateException("Unknown area scope.");
                        }
                    });
                })
                .subscribe(item -> {
                    items.add(item);
                    getView().onItemInserted(items.size() - 1);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onStopOverride() {
        super.onStopOverride();

        compositeDisposable.clear();
    }

    public int getItemCount() {
        return items.size();
    }

    @NonNull
    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public void onItemSelected(int position) {
        if (0 <= position) {
            selectedItem = items.get(position);
        } else {
            selectedItem = null;
        }

        getView().onUpdateButtonSelectEnabled(canSelect());
    }

    public void onClickButtonClose() {
        getView().onCloseView();
    }

    public void onClickButtonSelect() {
        getView().onAreaSettingsSelected(selectedItem.areaSettings, selectedItem.area, selectedItem.areaDescription);
        getView().onCloseView();
    }

    private boolean canSelect() {
        return selectedItem != null;
    }

    public final class ItemPresenter {

        private AreaSettingsItemView itemView;

        public void onCreateItemView(@NonNull AreaSettingsItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            Item item = items.get(position);

            int resId = (item.areaSettings.getAreaScopeAsEnum() == Scope.PUBLIC ? R.string.label_area_mode_public :
                    R.string.label_area_mode_user);
            itemView.onUpdateAreaMode(resId);
            itemView.onUpdateUpdatedAt(item.areaSettings.getUpdatedAtAsLong());
            itemView.onUpdateAreaName(item.area.getName());
            itemView.onUpdateAreaDescriptionName(item.areaDescription.getName());
        }
    }

    private final class Item {

        final AreaSettings areaSettings;

        Area area;

        AreaDescription areaDescription;

        Item(@NonNull AreaSettings areaSettings) {
            this.areaSettings = areaSettings;
        }
    }
}
