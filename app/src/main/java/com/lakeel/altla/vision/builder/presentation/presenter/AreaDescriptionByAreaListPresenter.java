package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionByAreaListView;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionItemView;
import com.lakeel.altla.vision.helper.AreaDescriptionNameComparater;
import com.lakeel.altla.vision.model.AreaDescription;
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

public final class AreaDescriptionByAreaListPresenter extends BasePresenter<AreaDescriptionByAreaListView>
        implements DataList.OnItemListener {

    private static final String ARG_AREA_SCOPE_VALUE = "areaScropeValue";

    private static final String ARG_AREA_ID = "areaId";

    @Inject
    VisionService visionService;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final List<AreaDescription> items = new ArrayList<>();

    private AreaScope areaScope;

    private String areaId;

    @Inject
    public AreaDescriptionByAreaListPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull AreaScope areaScope, @NonNull String areaId) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_AREA_SCOPE_VALUE, areaScope.getValue());
        bundle.putString(ARG_AREA_ID, areaId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        int areaScopeValue = arguments.getInt(ARG_AREA_SCOPE_VALUE, -1);
        if (areaScopeValue < 0) {
            throw new IllegalArgumentException(String.format("Argument '%s' is required.", ARG_AREA_ID));
        }

        AreaScope areaScope = AreaScope.toAreaScope(areaScopeValue);
        if (areaScope == AreaScope.UNKNOWN) throw new IllegalArgumentException("Unknown area scope.");


        String areaId = arguments.getString(ARG_AREA_ID, null);
        if (areaId == null) {
            throw new IllegalArgumentException(String.format("Argument '%s' must be not null.", ARG_AREA_ID));
        }

        this.areaScope = areaScope;
        this.areaId = areaId;
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();
        getView().onDataSetChanged();

        Disposable disposable = Single.<List<AreaDescription>>create(e -> {
            switch (areaScope) {
                case PUBLIC: {
                    visionService.getPublicAreaDescriptionApi()
                                 .findAreaDescriptionsByAreaId(areaId, areaDescriptions -> {
                                     Collections.sort(areaDescriptions, AreaDescriptionNameComparater.INSTANCE);
                                     e.onSuccess(areaDescriptions);
                                 }, e::onError);
                    break;
                }
                case USER: {
                    visionService.getUserAreaDescriptionApi()
                                 .findAreaDescriptionsByAreaId(areaId, areaDescriptions -> {
                                     Collections.sort(areaDescriptions, AreaDescriptionNameComparater.INSTANCE);
                                     e.onSuccess(areaDescriptions);
                                 }, e::onError);
                    break;
                }
            }
        }).subscribe(areaDescriptions -> {
            items.addAll(areaDescriptions);
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

    public void onClickItem(int position) {
        AreaDescription areaDescription = items.get(position);
        getView().onItemSelected(areaDescription.getId());
    }

    public final class ItemPresenter {

        private UserAreaDescriptionItemView itemView;

        public void onCreateItemView(@NonNull UserAreaDescriptionItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            AreaDescription areaDescription = items.get(position);
            itemView.onUpdateAreaDescriptionId(areaDescription.getId());
            itemView.onUpdateName(areaDescription.getName());
        }
    }
}
