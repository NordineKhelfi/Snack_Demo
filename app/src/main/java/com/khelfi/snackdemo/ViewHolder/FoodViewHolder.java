package com.khelfi.snackdemo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.khelfi.snackdemo.Interfaces.ItemClickListener;
import com.khelfi.snackdemo.R;

/**
 * Created by norma on 24/12/2017.
 *
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView ivFood;
    public TextView tvFood;

    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);

        ivFood = (ImageView) itemView.findViewById(R.id.ivFoodItem);
        tvFood = (TextView) itemView.findViewById(R.id.tvFoodName);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);   // <-- Understand this..
    }


}
