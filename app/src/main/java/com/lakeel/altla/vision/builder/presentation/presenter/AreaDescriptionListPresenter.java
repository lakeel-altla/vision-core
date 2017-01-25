package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.presentation.model.AreaDescriptionModel;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionListItemView;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionListView;
import com.lakeel.altla.vision.domain.usecase.FindAllUserAreaDescriptionsUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class AreaDescriptionListPresenter {

    private static final Log LOG = LogFactory.getLog(AreaDescriptionListPresenter.class);

    @Inject
    FindAllUserAreaDescriptionsUseCase findAllUserAreaDescriptionsUseCase;

    @Inject
    GoogleApiClient googleApiClient;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final List<AreaDescriptionModel> items = new ArrayList<>();

    private AreaDescriptionListView view;

    @Inject
    public AreaDescriptionListPresenter() {
    }

    public void onCreateView(@NonNull AreaDescriptionListView view) {
        this.view = view;
    }

    public void onStart() {
        items.clear();

        Disposable disposable = findAllUserAreaDescriptionsUseCase
                .execute()
                .map(userAreaDescription -> {
                    LOG.d("name = %s", userAreaDescription.name);

                    AreaDescriptionModel model = new AreaDescriptionModel(userAreaDescription.areaDescriptionId);
                    model.name = userAreaDescription.name;
                    model.creationDate = new Date(userAreaDescription.creationTime);
                    model.placeId = userAreaDescription.placeId;
                    model.level = String.valueOf(userAreaDescription.level);
                    return model;
                })
                // TODO: to use-case
                .flatMap(model -> Observable.<AreaDescriptionModel>create(e -> {
                    if (model.placeId != null && model.placeId.length() != 0) {
                        Places.GeoDataApi.getPlaceById(googleApiClient, model.placeId)
                                         .setResultCallback(places -> {
                                             if (places.getStatus().isSuccess()) {
                                                 Place place = places.get(0);

                                                 model.placeName = place.getName().toString();
                                                 model.placeAddress = place.getAddress().toString();

                                             } else if (places.getStatus().isCanceled()) {
                                                 LOG.e("Getting the place was canceled: placeId = %s",
                                                       model.placeId);
                                             } else if (places.getStatus().isInterrupted()) {
                                                 LOG.e("Getting the place was interrupted: placeId = %s",
                                                       model.placeId);
                                             }
                                             e.onNext(model);
                                             e.onComplete();
                                         });
                    } else {
                        e.onNext(model);
                        e.onComplete();
                    }
                }))
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(models -> {
                    items.clear();
                    items.addAll(models);
                    view.updateItems();
                }, e -> {
                    LOG.e("Loading area description meta datas failed.", e);
                });
        compositeDisposable.add(disposable);
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    public void onCreateItemView(@NonNull AreaDescriptionListItemView itemView) {
        AreaDescriptionListPresenter.ItemPresenter itemPresenter = new AreaDescriptionListPresenter.ItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
    }

    public int getItemCount() {
        return items.size();
    }

    public final class ItemPresenter {

        private AreaDescriptionListItemView itemView;

        private void onCreateItemView(@NonNull AreaDescriptionListItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            AreaDescriptionModel model = items.get(position);
            itemView.showModel(model);
        }
    }
}
