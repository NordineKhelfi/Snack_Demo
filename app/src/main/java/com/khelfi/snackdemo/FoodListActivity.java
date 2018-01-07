package com.khelfi.snackdemo;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khelfi.snackdemo.Interfaces.ItemClickListener;
import com.khelfi.snackdemo.Model.Food;
import com.khelfi.snackdemo.ViewHolder.FoodViewHolder;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference food_table;
    String categoryId;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> recyclerAdapter;

    //Search functionality
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar searchBar;


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
            loadFoodList(categoryId);
        }


        //Search functionality

        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        loadSuggestion();
        searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(10);

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //While user is typing, we actualise the suggestions
                List<String> changingSuggestions = new ArrayList<>();

                for(String suggestion : suggestList){
                    if(suggestion.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        changingSuggestions.add(suggestion);
                }

                searchBar.setLastSuggestions(changingSuggestions);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }) ;


        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

                //When the searchBar is closed, we restore the original recyclerAdapter
                if(!enabled)
                    recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                //We set the reyclerAdapter corresponding to our research
                searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                        R.layout.food_item,
                        FoodViewHolder.class,
                        food_table.orderByChild("name").equalTo(text.toString())) {
                    @Override
                    protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                        viewHolder.tvFood.setText(model.getName());
                        Picasso.with(getApplicationContext()).load(model.getImageLink()).into(viewHolder.ivFood);

                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(FoodListActivity.this, FoodDetailActivity.class);
                                intent.putExtra("foodId", searchAdapter.getRef(position).getKey());
                                startActivity(intent)   ;

                            }
                        });
                    }
                };

                recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    private void loadFoodList(String categoryId) {
        //Fill the recyclerView
        recyclerAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                food_table.orderByChild("menuId").equalTo(categoryId)) {    // <-- " SELECT * FROM food_table WHERE menuId = 'categoryId' " in SQL

            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, final Food model, int position) {
                viewHolder.tvFood.setText(model.getName());
                Picasso.with(getApplicationContext()).load(model.getImageLink()).into(viewHolder.ivFood);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(FoodListActivity.this, FoodDetailActivity.class);
                        intent.putExtra("foodId", recyclerAdapter.getRef(position).getKey());
                        startActivity(intent)   ;

                    }
                });

            }
        };

        //finally Set the adapter
        recyclerView.setAdapter(recyclerAdapter);
    }


    private void loadSuggestion() {

        food_table.orderByChild("menuId").equalTo(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Food item = snapshot.getValue(Food.class);
                    suggestList.add(item.getName());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
