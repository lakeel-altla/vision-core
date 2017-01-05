package com.lakeel.altla.vision.builder.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.builder.presentation.model.AreaDescriptionModel;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionListItemView;
import com.lakeel.altla.vision.builder.presentation.view.AreaDescriptionListView;
import com.lakeel.altla.vision.domain.usecase.DeleteUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindAllUserAreaDescriptionsUseCase;
import com.lakeel.altla.vision.domain.usecase.GetAreaDescriptionCacheDirectoryUseCase;
import com.lakeel.altla.vision.domain.usecase.SaveUserAreaDescriptionUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public final class AreaDescriptionListPresenter {

    private static final Log LOG = LogFactory.getLog(AreaDescriptionListPresenter.class);

    @Inject
    FindAllUserAreaDescriptionsUseCase findAllUserAreaDescriptionsUseCase;

    @Inject
    GetAreaDescriptionCacheDirectoryUseCase getAreaDescriptionCacheDirectoryUseCase;

    @Inject
    SaveUserAreaDescriptionUseCase saveUserAreaDescriptionUseCase;

    @Inject
    DeleteUserAreaDescriptionUseCase deleteUserAreaDescriptionUseCase;

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private final List<AreaDescriptionModel> models = new ArrayList<>();

    private TangoWrapper tangoWrapper;

    private AreaDescriptionListView view;

    @Inject
    public AreaDescriptionListPresenter() {
    }

    public void onCreate(@NonNull TangoWrapper tangoWrapper) {
        this.tangoWrapper = tangoWrapper;
    }

    public void onCreateView(@NonNull AreaDescriptionListView view) {
        this.view = view;
    }

    public void onStart() {
        models.clear();
    }

    public void onStop() {
        compositeSubscription.clear();
    }

    public void onCreateItemView(@NonNull AreaDescriptionListItemView itemView) {
        AreaDescriptionListPresenter.ItemPresenter itemPresenter = new AreaDescriptionListPresenter.ItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
    }

    public int getItemCount() {
        return models.size();
    }

    public final class ItemPresenter {

        private AreaDescriptionListItemView itemView;

        private void onCreateItemView(@NonNull AreaDescriptionListItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            AreaDescriptionModel model = models.get(position);
            itemView.showModel(model);
        }
    }
}
