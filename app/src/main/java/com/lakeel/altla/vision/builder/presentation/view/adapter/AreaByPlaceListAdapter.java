package com.lakeel.altla.vision.builder.presentation.view.adapter;

import com.lakeel.altla.vision.builder.R;
import com.lakeel.altla.vision.builder.presentation.presenter.AreaByPlaceListPresenter;
import com.lakeel.altla.vision.builder.presentation.view.AreaByPlaceItemView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class AreaByPlaceListAdapter extends RecyclerView.Adapter<AreaByPlaceListAdapter.ViewHolder> {

    private final AreaByPlaceListPresenter presenter;

    private RecyclerView recyclerView;

    private LayoutInflater inflater;

    public AreaByPlaceListAdapter(@NonNull AreaByPlaceListPresenter presenter) {
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
        View itemView = inflater.inflate(R.layout.item_area, parent, false);
        itemView.setOnClickListener(v -> {
            if (recyclerView != null) {
                int position = recyclerView.getChildAdapterPosition(v);
                presenter.onClickItem(position);
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

    class ViewHolder extends RecyclerView.ViewHolder implements AreaByPlaceItemView {

        @BindView(R.id.text_view_name)
        TextView textViewName;

        @BindView(R.id.text_view_id)
        TextView textViewId;

        @BindView(R.id.text_view_level)
        TextView textViewLevel;

        private AreaByPlaceListPresenter.ItemPresenter itemPresenter;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemPresenter = presenter.createItemPresenter();
            itemPresenter.onCreateItemView(this);
        }

        @Override
        public void onUpdateAreaId(@NonNull String areaId) {
            textViewId.setText(areaId);
        }

        @Override
        public void onUpdateName(@Nullable String name) {
            textViewName.setText(name);
        }

        @Override
        public void onUpdateLevel(int level) {
            textViewLevel.setText(String.valueOf(level));
        }
    }
}
