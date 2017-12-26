package com.khelfi.snackdemo.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.khelfi.snackdemo.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by norma on 25/12/2017.
 *
 */

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "SnackDemoDB.db";
    private static final int DB_VERSION = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public List<Order> getCart(){

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"productId", "productName", "quantity", "price"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst()){

            do {
                result.add(new Order(c.getString(c.getColumnIndex("productId")),
                        c.getString(c.getColumnIndex("productName")),
                        c.getString(c.getColumnIndex("quantity")),
                        c.getString(c.getColumnIndex("price"))));

            }while (c.moveToNext());
        }

        return result;

    }


    public void addToCart(Order order){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT into OrderDetail (productId, productName, quantity, price) VALUES ('%s', '%s', '%s', '%s')",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice());

        db.execSQL(query);
    }

    public void clearCart(){

        SQLiteDatabase db = getReadableDatabase();
        String query = "DELETE FROM OrderDetail";   // <-- Does'n work with " DELETE * FROM ". Understant this
        db.execSQL(query);
    }

}
