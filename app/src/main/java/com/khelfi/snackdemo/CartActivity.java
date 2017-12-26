package com.khelfi.snackdemo;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khelfi.snackdemo.Common.Common;
import com.khelfi.snackdemo.Database.Database;
import com.khelfi.snackdemo.Model.Order;
import com.khelfi.snackdemo.Model.Request;
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

        bPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //AlertDialog to get user's address then submit the order.
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(CartActivity.this);

                //Add EditText to alert Dialog
                final EditText etAddress = new EditText(CartActivity.this);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );

                etAddress.setLayoutParams(layoutParams);

                adBuilder.setTitle("One more step !")
                        .setMessage("Enter your address")
                        .setView(etAddress)
                        .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                        .setPositiveButton("Let's go", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //Input check
                                if(etAddress.getText().toString().matches("")){
                                    Toast.makeText(CartActivity.this, "Please enter your address", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                //Create a new Request
                                Request request = new Request(Common.currentUser.getPhone(),
                                        Common.currentUser.getName(),
                                        etAddress.getText().toString(),
                                        tvTotal.getText().toString(),
                                        orderList);

                                //Submit request to Firebase (we'll use "System.currentTimeMillis as a key)
                                requests_table.child(String.valueOf(System.currentTimeMillis())).setValue(request);

                                //Clean cart
                                Database db = new Database(getApplicationContext());
                                db.clearCart();
                                
                                //Say goodbye
                                Toast.makeText(CartActivity.this, "Order placed.. Thank you !", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        adBuilder.show();




            }
        });

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
