package com.khelfi.snackdemo;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khelfi.snackdemo.Database.Database;
import com.khelfi.snackdemo.Model.Food;
import com.khelfi.snackdemo.Model.Order;
import com.squareup.picasso.Picasso;

public class FoodDetailActivity extends AppCompatActivity {

    TextView tvFoodName, tvFoodDescription, tvFoodPrice;
    ImageView ivFoodDetail;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton fabCart;
    ElegantNumberButton numberButton;

    String foodId = "";

    DatabaseReference food_table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        food_table = FirebaseDatabase.getInstance().getReference("Food");

        tvFoodName = (TextView) findViewById(R.id.tvFoodNameDetail);
        tvFoodDescription = (TextView) findViewById(R.id.tvFoodDescription);
        tvFoodPrice = (TextView) findViewById(R.id.tvFoodPrice);
        ivFoodDetail = (ImageView) findViewById(R.id.ivFoodDetail);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingTB);
        fabCart = (FloatingActionButton) findViewById(R.id.fabCart);
        numberButton = (ElegantNumberButton) findViewById(R.id.numberButton);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        if(getIntent() != null)
            foodId = getIntent().getStringExtra("foodId");

        if(!foodId.isEmpty()){

            //With the foodId passed from previous activity, we get food details from the DB
            food_table.child(foodId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Food food = dataSnapshot.getValue(Food.class);

                    tvFoodName.setText(food.getName());
                    tvFoodDescription.setText(food.getDescription());
                    tvFoodPrice.setText(food.getPrice());
                    Picasso.with(getApplicationContext()).load(food.getImageLink()).into(ivFoodDetail);

                    collapsingToolbarLayout.setTitle(food.getName());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //Add to cart
            fabCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Order order = new Order(foodId, tvFoodName.getText().toString(), numberButton.getNumber(), tvFoodPrice.getText().toString());

                    Database db = new Database(getApplicationContext());
                    db.addToCart(order);

                    Toast.makeText(getApplicationContext(), "Added to cart !", Toast.LENGTH_SHORT).show();
                }
            });

        }



    }
}
