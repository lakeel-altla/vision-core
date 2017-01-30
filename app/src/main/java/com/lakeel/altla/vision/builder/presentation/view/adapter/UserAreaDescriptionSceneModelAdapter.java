package com.lakeel.altla.vision.builder.presentation.view.adapter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionSceneModel;
import com.lakeel.altla.vision.builder.presentation.presenter.UserAreaDescriptionScenesPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionSceneItemView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserAreaDescriptionSceneModelAdapter
        extends RecyclerView.Adapter<UserAreaDescriptionSceneModelAdapter.ViewHolderAreaDescription> {

    private final UserAreaDescriptionScenesPresenter presenter;

    private LayoutInflater inflater;

    public UserAreaDescriptionSceneModelAdapter(@NonNull UserAreaDescriptionScenesPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ViewHolderAreaDescription onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        View view = inflater.inflate(R.layout.item_user_scene_model, parent, false);
        ViewHolderAreaDescription holder = new ViewHolderAreaDescription(view);
        presenter.onCreateItemView(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolderAreaDescription holder, int position) {
        holder.onBind(position);
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

        private UserAreaDescriptionScenesPresenter.ItemPresenter itemPresenter;

        private ViewHolderAreaDescription(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setItemPresenter(@NonNull UserAreaDescriptionScenesPresenter.ItemPresenter itemPresenter) {
            this.itemPresenter = itemPresenter;
        }

        @Override
        public void showModel(@NonNull UserAreaDescriptionSceneModel model) {
            textViewName.setText(model.name);
            textViewId.setText(model.sceneId);
        }

        private void onBind(int position) {
            itemPresenter.onBind(position);
        }
    }
}
