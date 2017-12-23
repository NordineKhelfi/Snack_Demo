package com.khelfi.snackdemo.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.khelfi.snackdemo.Interfaces.ItemClickListener;

/**
 * Our RecyclerView's ViewHolder.
 * Created by norma on 23/12/2017.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder implements ItemClickListener{

    public TextView tvMenuName;
    public ImageView ivMenu;

    public MenuViewHolder(View itemView) {
        super(itemView);

        //We are here now...
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {

    }
}
