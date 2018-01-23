package com.khelfi.snackdemo.Services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.khelfi.snackdemo.Common.Common;
import com.khelfi.snackdemo.Model.Token;

/**
 *Take a look here :
 * --> https://firebase.google.com/docs/cloud-messaging/android/client
 *
 * Created by norma on 22/01/2018.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();

        //Check
        if(Common.currentUser == null)
            return;

        //Update token to firebase DB

        DatabaseReference token_table = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefreshed, false);

        token_table.child(Common.currentUser.getPhone()).setValue(token);

    }
}
