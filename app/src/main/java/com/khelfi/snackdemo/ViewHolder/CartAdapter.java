package com.khelfi.snackdemo.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.khelfi.snackdemo.Interfaces.ItemClickListener;
import com.khelfi.snackdemo.Model.Order;
import com.khelfi.snackdemo.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This time, we also need a custom Adapter, to get data from our SQLite DB.
 * In this file, we'll handle both of the Adapter and the View Holder.
 *
 * Created by norma on 25/12/2017.
 */


class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView tvName, tvPrice;
    public ImageView ivNumber;
    private ItemClickListener itemClickListener;

    public CartViewHolder(View itemView) {
        super(itemView);

        tvName = (TextView) itemView.findViewById(R.id.tvCartItemName);
        tvPrice = (TextView) itemView.findViewById(R.id.tvCartItemPrice);
        ivNumber = (ImageView) itemView.findViewById(R.id.ivCartItemCount);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}




public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater =LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_item, parent, false);

        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {

        holder.tvName.setText( listData.get(position).getProductName() );

        Locale locale = new Locale("fr", "FR");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        double dPrice = Double.parseDouble(listData.get(position).getPrice()) * Double.parseDouble(listData.get(position).getQuantity());

        holder.tvPrice.setText(numberFormat.format(dPrice));

        TextDrawable txtDrawable = TextDrawable.builder()
                .buildRound("" + listData.get(position).getQuantity(), Color.parseColor("#C63C22"));

        holder.ivNumber.setImageDrawable(txtDrawable);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
