package com.lakeel.altla.vision.builder.presentation.presenter;

import com.google.android.gms.location.places.Place;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.AreaByPlaceItemView;
import com.lakeel.altla.vision.builder.presentation.view.AreaByPlaceListView;
import com.lakeel.altla.vision.helper.AreaNameComparater;
import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.model.AreaScope;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class AreaByPlaceListPresenter extends BasePresenter<AreaByPlaceListView>
        implements DataList.OnItemListener {

    private static final String ARG_AREA_SCOPE_VALUE = "areaScopeValue";

    private static final String ARG_PLACE_ID = "placeId";

    @Inject
    VisionService visionService;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final List<Area> items = new ArrayList<>();

    private AreaScope areaScope;

    private String placeId;

    private Area selectedArea;

    @Inject
    public AreaByPlaceListPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull AreaScope areaScope, @NonNull Place place) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_AREA_SCOPE_VALUE, areaScope.getValue());
        bundle.putString(ARG_PLACE_ID, place.getId());
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        int areaScopeValue = arguments.getInt(ARG_AREA_SCOPE_VALUE, -1);
        if (areaScopeValue < 0) {
            throw new IllegalArgumentException(String.format("Argument '%s' is required.", ARG_AREA_SCOPE_VALUE));
        }
        areaScope = AreaScope.toAreaScope(areaScopeValue);

        String placeId = arguments.getString(ARG_PLACE_ID);
        if (placeId == null) {
            throw new IllegalArgumentException(String.format("Argument '%s' is required.", ARG_PLACE_ID));
        }

        this.placeId = placeId;
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

        Disposable disposable = Single.<List<Area>>create(e -> {
            switch (areaScope) {
                case PUBLIC: {
                    visionService.getPublicAreaApi().findAreasByPlaceId(placeId, areas -> {
                        Collections.sort(areas, AreaNameComparater.INSTANCE);
                        e.onSuccess(areas);
                    }, e::onError);
                    break;
                }
                case USER: {
                    visionService.getUserAreaApi().findAreasByPlaceId(placeId, areas -> {
                        Collections.sort(areas, AreaNameComparater.INSTANCE);
                        e.onSuccess(areas);
                    }, e::onError);
                    break;
                }
            }
        }).subscribe(areas -> {
            items.addAll(areas);
            getView().onDataSetChanged();
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

    public void onItemSelected(int position) {
        if (0 <= position) {
            selectedArea = items.get(position);
        } else {
            selectedArea = null;
        }

        getView().onUpdateButtonSelectEnabled(canSelect());
    }

    public void onClickButtonPrevious() {
        getView().onBackToAreaFindView();
    }

    public void onClickButtonSelect() {
        getView().onAreaSelected(selectedArea);
        getView().onCloseView();
    }

    private boolean canSelect() {
        return selectedArea != null;
    }

    public final class ItemPresenter {

        private AreaByPlaceItemView itemView;

        public void onCreateItemView(@NonNull AreaByPlaceItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            Area area = items.get(position);
            itemView.onUpdateAreaId(area.getId());
            itemView.onUpdateName(area.getName());
            itemView.onUpdateLevel(area.getLevel());
        }
    }
}
