package com.khelfi.snackdemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.khelfi.snackdemo.Common.Common;
import com.khelfi.snackdemo.Interfaces.ItemClickListener;
import com.khelfi.snackdemo.Model.Order;
import com.khelfi.snackdemo.Model.Request;
import com.khelfi.snackdemo.ViewHolder.OrderViewHolder;

public class OrderActivity extends AppCompatActivity {

    public RecyclerView recyclerView;

    DatabaseReference requests;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> recyclerAdapter;

    String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_order);
        recyclerView.setHasFixedSize(true);

        //Firebase
        requests = FirebaseDatabase.getInstance().getReference("Requests");

        //If this activity is called for the first time from an outside notification, we get the phoneNumber from notification's pendingIntent
        if(getIntent().hasExtra("userPhone"))
            loadOrders(getIntent().getStringExtra("userPhone"));
        else
            loadOrders(Common.currentUser.getPhone());


    }

    @Override
    protected void onStop() {
        super.onStop();
        recyclerAdapter.stopListening();
    }

    private void loadOrders(String phone) {

        Query query = requests.orderByChild("phone").equalTo(phone);

        FirebaseRecyclerOptions<Request> recyclerOptions = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(query, Request.class)
                .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Request model) {

                holder.tvId.setText(recyclerAdapter.getRef(position).getKey());
                holder.tvStatus.setText(codeToStatus(model.getStatus()));
                holder.tvPhone.setText(model.getPhone());
                holder.tvAddress.setText(model.getAddress());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }

            @Override
            public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
                return new OrderViewHolder(itemView);
            }
        };

        recyclerAdapter.startListening();

        recyclerView.setAdapter(recyclerAdapter);

    }

    public static String codeToStatus(String code) {

        switch (code){

            case "0" :
                return "Placed";

            case "1" :
                return "On the way..";

            case "2" :
                return "Delivered";

            default:
                return "Placed";
        }

    }
}
