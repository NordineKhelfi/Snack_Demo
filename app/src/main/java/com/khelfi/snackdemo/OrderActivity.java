package com.khelfi.snackdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khelfi.snackdemo.Common.Common;
import com.khelfi.snackdemo.Model.Request;
import com.khelfi.snackdemo.ViewHolder.OrderViewHolder;

public class OrderActivity extends AppCompatActivity {

    public RecyclerView recyclerView;

    DatabaseReference requests;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_order);
        recyclerView.setHasFixedSize(true);

        //Firebase
        requests = FirebaseDatabase.getInstance().getReference("Requests");

        loadOrders(Common.currentUser.getPhone());


    }

    private void loadOrders(String phone) {

        recyclerAdapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class,
                R.layout.order_item,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)) {    // <-- "SELECT * FROM Requests WHERE phone = 'phone' "


            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {

                viewHolder.tvId.setText(recyclerAdapter.getRef(position).getKey());
                viewHolder.tvStatus.setText(codeToStatus(model.getStatus()));
                viewHolder.tvPhone.setText(model.getPhone());
                viewHolder.tvAddress.setText(model.getAddress());
            }
        };

        recyclerView.setAdapter(recyclerAdapter);

    }

    private String codeToStatus(String code) {

        switch (code){

            case "0" :
                return "Placed";

            case "1" :
                return "On my way..";

            case "2" :
                return "Delivered";

            default:
                return "Placed";
        }

    }
}
