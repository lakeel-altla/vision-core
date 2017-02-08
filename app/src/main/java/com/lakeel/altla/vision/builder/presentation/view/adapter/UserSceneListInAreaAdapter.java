package com.lakeel.altla.vision.builder.presentation.view.adapter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.UserSceneItemModel;
import com.lakeel.altla.vision.builder.presentation.presenter.UserSceneListInAreaPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserSceneItemView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserSceneListInAreaAdapter
        extends RecyclerView.Adapter<UserSceneListInAreaAdapter.ViewHolder> {

    private final UserSceneListInAreaPresenter presenter;

    private LayoutInflater inflater;

    public UserSceneListInAreaAdapter(@NonNull UserSceneListInAreaPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        View view = inflater.inflate(R.layout.item_user_scene, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemPresenter.onBind(position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    final class ViewHolder extends RecyclerView.ViewHolder implements UserSceneItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.text_view_id)
        TextView textViewId;

        private final UserSceneListInAreaPresenter.ItemPresenter itemPresenter;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemPresenter = presenter.createItemPresenter();
            itemPresenter.onCreateItemView(this);
        }

        @Override
        public void onModelUpdated(@NonNull UserSceneItemModel model) {
            textViewName.setText(model.name);
            textViewId.setText(model.sceneId);
        }
    }
}
