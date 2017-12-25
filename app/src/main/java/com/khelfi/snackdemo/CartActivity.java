package com.khelfi.snackdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khelfi.snackdemo.Database.Database;
import com.khelfi.snackdemo.Model.Order;
import com.khelfi.snackdemo.ViewHolder.CartAdapter;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView tvTotal;
    Button bPlaceOrder;

    List<Order> orderList;

    //Firebase
    DatabaseReference requests_table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Init Firebase
        requests_table = FirebaseDatabase.getInstance().getReference("Requests");

        //UI init
        recyclerView = (RecyclerView) findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(true);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        bPlaceOrder = (Button) findViewById(R.id.bPlaceOrder);

        loadCart();

    }

    private void loadCart() {

        Database db = new Database(this);
        orderList = db.getCart();

        CartAdapter cartAdapter = new CartAdapter(orderList, this);
        recyclerView.setAdapter(cartAdapter);

        //Calculate total price;
        double dTotal = 0.0;
        for(Order order : orderList){
            dTotal += Double.parseDouble(order.getPrice()) * Double.parseDouble(order.getQuantity());
        }

        Locale locale = new Locale("fr", "FR");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        tvTotal.setText(numberFormat.format(dTotal));

    }
}
