package com.lakeel.altla.vision.builder.presentation.view.adapter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionSceneModel;
import com.lakeel.altla.vision.builder.presentation.presenter.UserAreaDescriptionSceneListPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionSceneItemView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserAreaDescriptionSceneListAdapter
        extends RecyclerView.Adapter<UserAreaDescriptionSceneListAdapter.ViewHolderAreaDescription> {

    private final UserAreaDescriptionSceneListPresenter presenter;

    private LayoutInflater inflater;

    public UserAreaDescriptionSceneListAdapter(@NonNull UserAreaDescriptionSceneListPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ViewHolderAreaDescription onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        View view = inflater.inflate(R.layout.item_user_scene_model, parent, false);
        return new ViewHolderAreaDescription(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderAreaDescription holder, int position) {
        holder.itemPresenter.onBind(position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    final class ViewHolderAreaDescription extends RecyclerView.ViewHolder implements UserAreaDescriptionSceneItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.text_view_id)
        TextView textViewId;

        private final UserAreaDescriptionSceneListPresenter.ItemPresenter itemPresenter;

        private ViewHolderAreaDescription(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemPresenter = presenter.createItemPresenter();
            itemPresenter.onCreateItemView(this);
        }

        @Override
        public void onModelUpdated(@NonNull UserAreaDescriptionSceneModel model) {
            textViewName.setText(model.name);
            textViewId.setText(model.sceneId);
        }
    }
}
