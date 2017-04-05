package com.lakeel.altla.vision.builder.presentation.view.adapter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.helper.DateFormatHelper;
import com.lakeel.altla.vision.builder.presentation.presenter.AreaSettingsListPresenter;
import com.lakeel.altla.vision.builder.presentation.view.AreaSettingsItemView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class AreaSettingsListAdapter extends RecyclerView.Adapter<AreaSettingsListAdapter.ViewHolder> {

    private final AreaSettingsListPresenter presenter;

    private RecyclerView recyclerView;

    private LayoutInflater inflater;

    private View selectedItem;

    public AreaSettingsListAdapter(@NonNull AreaSettingsListPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        View itemView = inflater.inflate(R.layout.item_area_settings, parent, false);
        itemView.setOnClickListener(v -> {
            if (recyclerView != null) {
                int position = recyclerView.getChildAdapterPosition(itemView);

                if (selectedItem == null) {
                    selectedItem = itemView;
                    selectedItem.setSelected(true);
                    presenter.onItemSelected(position);
                } else {
                    if (selectedItem == itemView) {
                        itemView.setSelected(false);
                        presenter.onItemSelected(-1);
                    } else {
                        selectedItem.setSelected(false);
                        selectedItem = itemView;
                        selectedItem.setSelected(true);
                        presenter.onItemSelected(position);
                    }
                }
            }
        });
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

    class ViewHolder extends RecyclerView.ViewHolder implements AreaSettingsItemView {

        @BindView(R.id.text_view_updated_at)
        TextView textViewUpdatedAt;

        @BindView(R.id.text_view_area_mode)
        TextView textViewAreaMode;

        @BindView(R.id.text_view_area_name)
        TextView textViewAreaName;

        @BindView(R.id.text_view_area_description_name)
        TextView textViewAreaDescriptionName;

        private AreaSettingsListPresenter.ItemPresenter itemPresenter;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemPresenter = presenter.createItemPresenter();
            itemPresenter.onCreateItemView(this);
        }

        @Override
        public void onUpdateUpdatedAt(long updatedAt) {
            String text = DateFormatHelper.format(itemView.getContext(), updatedAt);
            textViewUpdatedAt.setText(text);
        }

        @Override
        public void onUpdateAreaMode(@StringRes int resId) {
            textViewAreaMode.setText(resId);
        }

        @Override
        public void onUpdateAreaName(@Nullable String areaName) {
            textViewAreaName.setText(areaName);
        }

        @Override
        public void onUpdateAreaDescriptionName(@Nullable String areaDescriptionName) {
            textViewAreaDescriptionName.setText(areaDescriptionName);
        }
    }
}
