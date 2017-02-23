package com.lakeel.altla.vision.builder.presentation.view.adapter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.UserActorImageModel;
import com.lakeel.altla.vision.builder.presentation.presenter.UserActorImageListPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserActorImageItemView;
import com.lakeel.altla.vision.builder.presentation.view.helper.ThumbnailLoader;

import android.content.ClipData;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnLongClick;

public final class UserActorImageListAdapter extends RecyclerView.Adapter<UserActorImageListAdapter.ViewHolder> {

    private static final String INTENT_LABEL = "userActorImageModel";

    private final UserActorImageListPresenter presenter;

    private final ThumbnailLoader thumbnailLoader;

    private LayoutInflater inflater;

    public UserActorImageListAdapter(@NonNull UserActorImageListPresenter presenter, @NonNull Context context) {
        this.presenter = presenter;
        thumbnailLoader = new ThumbnailLoader(context);
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        View itemView = inflater.inflate(R.layout.item_user_actor_image, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemPresenter.onBind(position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements UserActorImageItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.image_view_thumbnail)
        ImageView imageViewThumbnail;

        private UserActorImageListPresenter.ItemPresenter itemPresenter;

        private final View.DragShadowBuilder dragShadowBuilder;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemPresenter = presenter.createItemPresenter();
            itemPresenter.onCreateItemView(this);

            dragShadowBuilder = new View.DragShadowBuilder(imageViewThumbnail);

            imageViewThumbnail.setOnDragListener((view, dragEvent) -> {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // returns true to accept a drag event.
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        return true;
                    case DragEvent.ACTION_DROP:
                        // does not accept to drop here.
                        return false;
                    case DragEvent.ACTION_DRAG_ENDED:
                        return true;
                }

                return false;
            });
        }

        @Override
        public void onUpdateName(@NonNull String name) {
            textViewName.setText(name);
        }

        @Override
        public void onUpdateThumbnail(@NonNull Uri uri) {
            thumbnailLoader.load(uri, imageViewThumbnail);
        }

        @Override
        public void onStartDrag(@NonNull UserActorImageModel model) {
            ClipData clipData = ClipData.newIntent(INTENT_LABEL, model.createIntent());
            imageViewThumbnail.startDrag(clipData, dragShadowBuilder, null, 0);
        }

        @OnLongClick(R.id.view_top)
        boolean onLongClickViewTop() {
            itemPresenter.onLongClickViewTop(getAdapterPosition());
            return true;
        }
    }
}
