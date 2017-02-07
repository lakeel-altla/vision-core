package com.lakeel.altla.vision.builder.presentation.view.adapter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionItemModel;
import com.lakeel.altla.vision.builder.presentation.presenter.UserAreaDescriptionListPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionItemView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserAreaDescriptionListAdapter
        extends RecyclerView.Adapter<UserAreaDescriptionListAdapter.ViewHolderUser> {

    private final UserAreaDescriptionListPresenter presenter;

    private LayoutInflater inflater;

    public UserAreaDescriptionListAdapter(@NonNull UserAreaDescriptionListPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ViewHolderUser onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        View view = inflater.inflate(R.layout.item_user_area_description_model, parent, false);
        return new ViewHolderUser(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderUser holder, int position) {
        holder.itemPresenter.onBind(position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    final class ViewHolderUser extends RecyclerView.ViewHolder implements UserAreaDescriptionItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.text_view_id)
        TextView textViewId;

        private final UserAreaDescriptionListPresenter.ItemPresenter itemPresenter;

        private ViewHolderUser(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemPresenter = presenter.createItemPresenter();
            itemPresenter.onCreateItemView(this);

            itemView.setOnClickListener(v -> itemPresenter.onClick(getAdapterPosition()));
        }

        @Override
        public void onModelUpdated(@NonNull UserAreaDescriptionItemModel model) {
            textViewName.setText(model.name);
            textViewId.setText(model.areaDescriptionId);
        }
    }
}
