package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.builder.presentation.model.AreaDescriptionModel;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionListItemView;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionListView;
import com.lakeel.altla.vision.domain.usecase.FindAllTangoAreaDescriptionsUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class AreaDescriptionListPresenter implements TangoWrapper.OnTangoReadyListener {

    private static final Log LOG = LogFactory.getLog(AreaDescriptionListPresenter.class);

    @Inject
    FindAllTangoAreaDescriptionsUseCase findAllTangoAreaDescriptionsUseCase;

    @Inject
    TangoWrapper tangoWrapper;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final List<AreaDescriptionModel> items = new ArrayList<>();

    private String currentAreaDescriptionId;

    private AreaDescriptionListView view;

    @Inject
    public AreaDescriptionListPresenter() {
    }

    @Override
    public void onTangoReady(Tango tango) {
        Disposable disposable = findAllTangoAreaDescriptionsUseCase
                .execute(tangoWrapper.getTango())
                .map(tangoAreaDescription -> {
                    AreaDescriptionModel model = new AreaDescriptionModel(tangoAreaDescription.areaDescriptionId,
                                                                          tangoAreaDescription.name,
                                                                          tangoAreaDescription.creationTime);
                    model.current = (model.areaDescriptionId.equals(currentAreaDescriptionId));
                    return model;
                })
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

    public void onCreate(String currentAreaDescriptionId) {
        this.currentAreaDescriptionId = currentAreaDescriptionId;
    }

    public void onCreateView(@NonNull AreaDescriptionListView view) {
        this.view = view;
    }

    public void onStart() {
        items.clear();
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    public void onResume() {
        tangoWrapper.addOnTangoReadyListener(this);
    }

    public void onPause() {
        tangoWrapper.removeOnTangoReadyListener(this);
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

        public void onClickImageButtonLoad(int position) {
            AreaDescriptionModel model = items.get(position);
            if (!model.current) {
                view.loadAreaDescription(model.areaDescriptionId);
            }
        }

        public void onClickImageButtonUnload(int position) {
            AreaDescriptionModel model = items.get(position);
            if (model.current) {
                view.loadAreaDescription(null);
            }
        }
    }
}
