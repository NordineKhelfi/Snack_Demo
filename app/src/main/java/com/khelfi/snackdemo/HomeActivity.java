package com.khelfi.snackdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.khelfi.snackdemo.Common.Common;
import com.khelfi.snackdemo.Interfaces.ItemClickListener;
import com.khelfi.snackdemo.Model.Category;
import com.khelfi.snackdemo.Model.Token;
import com.khelfi.snackdemo.ViewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvUser;
    DatabaseReference category_table;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> recyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(cartIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set user name
        View headerView = navigationView.getHeaderView(0);
        tvUser = (TextView) headerView.findViewById(R.id.tvUser);
        tvUser.setText(Common.currentUser.getName());

        //Init firebase
        category_table = FirebaseDatabase.getInstance().getReference("Category");

        //Load menu from DB
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        FirebaseRecyclerOptions<Category> recyclerOptions = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category_table, Category.class)
                .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {

                holder.tvMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getLink()).into(holder.ivMenu);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Send CategoryID to the new activity
                        Intent intent = new Intent(HomeActivity.this, FoodListActivity.class);
                        intent.putExtra("CategoryId", recyclerAdapter.getRef(position).getKey());   // <-- FirebaseUI features ... Awesome
                        startActivity(intent);
                    }
                });
            }

            @Override
            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(itemView);
            }
        };

        recyclerAdapter.startListening();

        recyclerView.setAdapter(recyclerAdapter);

        updateTokenToFirebaseDB(FirebaseInstanceId.getInstance().getToken());

    }

    @Override
    protected void onStop() {
        super.onStop();
        recyclerAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    private void updateTokenToFirebaseDB(String refreshedToken) {

        DatabaseReference token_table = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(refreshedToken, false);

        token_table.child(Common.currentUser.getPhone()).setValue(token);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*// Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(cartIntent);
        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(HomeActivity.this, OrderActivity.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_exit) {
            Intent exitIntent = new Intent(HomeActivity.this, SignInActivity.class);
            exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);   // --> https://developer.android.com/reference/android/content/Intent.html#FLAG_ACTIVITY_CLEAR_TASK
            startActivity(exitIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
