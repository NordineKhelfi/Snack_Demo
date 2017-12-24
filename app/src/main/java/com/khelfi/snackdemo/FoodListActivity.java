package com.khelfi.snackdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khelfi.snackdemo.Interfaces.ItemClickListener;
import com.khelfi.snackdemo.Model.Food;
import com.khelfi.snackdemo.ViewHolder.FoodViewHolder;
import com.squareup.picasso.Picasso;

public class FoodListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference food_table;
    String categoryId;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Firebase
        food_table = FirebaseDatabase.getInstance().getReference("Food");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);

        if(getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");

        if(!categoryId.isEmpty() && categoryId != null){

            //Fill the recyclerView
            recyclerAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                    R.layout.food_item,
                    FoodViewHolder.class,
                    food_table.orderByChild("menuId").equalTo(categoryId)) {    // <-- "SELECT * FROM food_table WHERE menuId = categoryId" in SQL

                @Override
                protected void populateViewHolder(FoodViewHolder viewHolder, final Food model, int position) {
                    viewHolder.tvFood.setText(model.getName());
                    Picasso.with(getApplicationContext()).load(model.getImageLink()).into(viewHolder.ivFood);

                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Toast.makeText(getApplicationContext(), "This is " + model.getName(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            };

            //finally Set the adapter
            recyclerView.setAdapter(recyclerAdapter);

        }


    }
}
