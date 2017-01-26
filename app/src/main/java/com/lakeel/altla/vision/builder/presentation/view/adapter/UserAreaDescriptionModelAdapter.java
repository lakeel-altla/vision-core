package com.lakeel.altla.vision.builder.presentation.view.adapter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.model.UserAreaDescriptionModel;
import com.lakeel.altla.vision.builder.presentation.presenter.UserAreaDescriptionListPresenter;
import com.lakeel.altla.vision.builder.presentation.view.UserAreaDescriptionListItemView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class UserAreaDescriptionModelAdapter
        extends RecyclerView.Adapter<UserAreaDescriptionModelAdapter.ViewHolderUser> {

    private final UserAreaDescriptionListPresenter presenter;

    private LayoutInflater inflater;

    public UserAreaDescriptionModelAdapter(@NonNull UserAreaDescriptionListPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ViewHolderUser onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        View view = inflater.inflate(R.layout.item_area_description_model, parent, false);
        ViewHolderUser holder = new ViewHolderUser(view);
        presenter.onCreateItemView(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolderUser holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    final class ViewHolderUser extends RecyclerView.ViewHolder implements UserAreaDescriptionListItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.text_view_id)
        TextView textViewId;

        @BindView(R.id.text_view_place_name)
        TextView textViewPlaceName;

        @BindView(R.id.text_view_place_address)
        TextView textViewPlaceAddress;

        @BindView(R.id.text_view_place_level)
        TextView textViewPlaceLevel;

        private UserAreaDescriptionListPresenter.ItemPresenter itemPresenter;

        private ViewHolderUser(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setItemPresenter(@NonNull UserAreaDescriptionListPresenter.ItemPresenter itemPresenter) {
            this.itemPresenter = itemPresenter;
        }

        @Override
        public void showModel(@NonNull UserAreaDescriptionModel model) {
            textViewName.setText(model.name);
            textViewId.setText(model.areaDescriptionId);
            textViewPlaceName.setText(model.placeName);
            textViewPlaceAddress.setText(model.placeAddress);
            textViewPlaceLevel.setText(model.level);
        }

        private void onBind(int position) {
            itemPresenter.onBind(position);
        }
    }
}
