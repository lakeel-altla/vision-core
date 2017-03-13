package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionInAreaListView;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionItemView;
import com.lakeel.altla.vision.domain.model.AreaDescription;
import com.lakeel.altla.vision.domain.model.AreaScope;
import com.lakeel.altla.vision.domain.usecase.FindAreaDescriptionsByAreaIdUseCase;
import com.lakeel.altla.vision.domain.usecase.FindUserAreaUseCase;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;
import com.lakeel.altla.vision.presentation.presenter.model.DataList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class AreaDescriptionInAreaListPresenter extends BasePresenter<AreaDescriptionInAreaListView>
        implements DataList.OnItemListener {

    @Inject
    FindAreaDescriptionsByAreaIdUseCase findAreaDescriptionsByAreaIdUseCase;

    @Inject
    FindUserAreaUseCase findUserAreaUseCase;

    private static final String ARG_AREA_SCOPE_VALUE = "areaScropeValue";

    private static final String ARG_AREA_ID = "areaId";

    private final List<AreaDescription> items = new ArrayList<>();

    private AreaScope areaScope;

    private String areaId;

    @Inject
    public AreaDescriptionInAreaListPresenter() {
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

        Disposable disposable = findAreaDescriptionsByAreaIdUseCase
                .execute(areaScope, areaId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(areaDescriptions -> {
                    items.addAll(areaDescriptions);
                    getView().onDataSetChanged();
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
