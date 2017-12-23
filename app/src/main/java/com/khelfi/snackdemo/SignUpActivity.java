package com.khelfi.snackdemo;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khelfi.snackdemo.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;

public class SignUpActivity extends AppCompatActivity {

    MaterialEditText etPhone, etName, etPass;
    Button bSignUp;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etPhone = (MaterialEditText) findViewById(R.id.etPhone);
        etName = (MaterialEditText) findViewById(R.id.etName);
        etPass = (MaterialEditText) findViewById(R.id.etPass);
        bSignUp = (Button) findViewById(R.id.bSignUp);
        dialog = new SpotsDialog(this);

        //Init Firebase
        final DatabaseReference user_table = FirebaseDatabase.getInstance().getReference("User");

        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Input check
                if(etPhone.getText().toString().matches("") || etName.getText().toString().matches("") ||etPass.getText().toString().matches(""))
                    return;

                dialog.show();

                user_table.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Check if user already registred in DB
                        if(dataSnapshot.child(etPhone.getText().toString()).exists()){
                            Toast.makeText(getApplicationContext(), "This number is already registred", Toast.LENGTH_SHORT).show();
                        } else{
                            //Put new user into DB table
                            User user = new User(etName.getText().toString(), etPass.getText().toString());
                            user_table.child(etPhone.getText().toString()).setValue(user);
                            Toast.makeText(getApplicationContext(), "Welcome to Snack Demo ! You can now login.", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }
}
