package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionModel;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionListItemView;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionListView;
import com.lakeel.altla.vision.domain.usecase.FindAllUserAreaDescriptionsUseCase;
import com.lakeel.altla.vision.domain.usecase.GetPlaceUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionListPresenter {

    private static final Log LOG = LogFactory.getLog(UserAreaDescriptionListPresenter.class);

    @Inject
    FindAllUserAreaDescriptionsUseCase findAllUserAreaDescriptionsUseCase;

    @Inject
    GetPlaceUseCase getPlaceUseCase;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final List<UserAreaDescriptionModel> items = new ArrayList<>();

    private UserAreaDescriptionListView view;

    @Inject
    public UserAreaDescriptionListPresenter() {
    }

    public void onCreateView(@NonNull UserAreaDescriptionListView view) {
        this.view = view;
    }

    public void onStart() {
        items.clear();

        Disposable disposable = findAllUserAreaDescriptionsUseCase
                .execute()
                .map(userAreaDescription -> {
                    UserAreaDescriptionModel model =
                            new UserAreaDescriptionModel(userAreaDescription.areaDescriptionId);
                    model.name = userAreaDescription.name;
                    model.creationDate = new Date(userAreaDescription.creationTime);
                    model.placeId = userAreaDescription.placeId;
                    model.level = String.valueOf(userAreaDescription.level);
                    return model;
                })
                .concatMap(model -> {
                    // Load the place information.
                    if (model.placeId != null) {
                        return getPlaceUseCase.execute(model.placeId)
                                              .map(place -> {
                                                  model.placeName = place.getName().toString();
                                                  model.placeAddress = place.getAddress().toString();
                                                  return model;
                                              })
                                              .toObservable();
                    } else {
                        return Observable.just(model);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.add(model);
                    view.updateItem(items.size() - 1);
                }, e -> {
                    LOG.e("Failed to find all user area descriptions.", e);
                }, () -> {
                    LOG.d("Found all user area description.");
                });
        compositeDisposable.add(disposable);
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    public void onCreateItemView(@NonNull UserAreaDescriptionListItemView itemView) {
        UserAreaDescriptionListPresenter.ItemPresenter itemPresenter = new UserAreaDescriptionListPresenter.ItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
    }

    public int getItemCount() {
        return items.size();
    }

    public final class ItemPresenter {

        private UserAreaDescriptionListItemView itemView;

        private void onCreateItemView(@NonNull UserAreaDescriptionListItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            UserAreaDescriptionModel model = items.get(position);
            itemView.showModel(model);
        }
    }
}
