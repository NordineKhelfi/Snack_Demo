package com.khelfi.snackdemo;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.khelfi.snackdemo.Common.Common;
import com.khelfi.snackdemo.Database.Database;
import com.khelfi.snackdemo.Model.Message;
import com.khelfi.snackdemo.Model.MyResponse;
import com.khelfi.snackdemo.Model.Notification;
import com.khelfi.snackdemo.Model.Order;
import com.khelfi.snackdemo.Model.Request;
import com.khelfi.snackdemo.Model.Token;
import com.khelfi.snackdemo.RemoteWebServer.APIService;
import com.khelfi.snackdemo.ViewHolder.CartAdapter;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView tvTotal;
    Button bPlaceOrder;

    List<Order> orderList;

    //Firebase
    DatabaseReference requests_table;

    //FCM Servicce
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Init Firebase
        requests_table = FirebaseDatabase.getInstance().getReference("Requests");

        //Init FCM Service
        mService = Common.getFCMService();

        //UI init
        recyclerView = (RecyclerView) findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(true);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        bPlaceOrder = (Button) findViewById(R.id.bPlaceOrder);

        loadCart();

        bPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Cart check
                if(orderList.isEmpty()){
                    Toast.makeText(CartActivity.this, "Your cart is empty..", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                                String orderKey = String.valueOf(System.currentTimeMillis());
                                requests_table.child(orderKey).setValue(request);

                                //Also send a notification to StaffSide's App via FCM
                                sendOrderNotification(orderKey);

                                //Clean cart
                                Database db = new Database(getApplicationContext());
                                db.clearCart();

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

    private void sendOrderNotification(final String orderKey) {

        /*
        We construct our message payload, like this :
        --> {
                to : "Token...",
                notification : {
                    title : "our title",
                    body : "Lorem ipsum blablabla..."
                }
            }
        */

        DatabaseReference token_table = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = token_table.orderByChild("isServerToken").equalTo(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token serverToken = snapshot.getValue(Token.class);

                    Notification notification = new Notification("Snack Demo", "You have a new order : #" + orderKey);
                    Message message = new Message(serverToken.getToken(), notification);

                    mService.sendNotification(message)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    if(response.code() == 200){
                                        if(response.body().success == 1){
                                            //Say goodbye
                                            Toast.makeText(CartActivity.this, "Order placed.. Thank you !", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else
                                            Toast.makeText(CartActivity.this, "Order Failed .. !", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());
                                }
                            });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
