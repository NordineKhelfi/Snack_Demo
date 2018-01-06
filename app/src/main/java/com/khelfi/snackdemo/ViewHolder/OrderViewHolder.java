package com.khelfi.snackdemo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.khelfi.snackdemo.Interfaces.ItemClickListener;
import com.khelfi.snackdemo.R;

/**
 * Created by norma on 06/01/2018.
 *
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView tvId, tvStatus, tvPhone, tvAddress;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        tvId = (TextView) itemView.findViewById(R.id.tvOrderId);
        tvStatus = (TextView) itemView.findViewById(R.id.tvOrderStatus);
        tvPhone = (TextView) itemView.findViewById(R.id.tvOrderPhone);
        tvAddress = (TextView) itemView.findViewById(R.id.tvOrderAddress);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
