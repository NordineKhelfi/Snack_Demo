package com.khelfi.snackdemo.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khelfi.snackdemo.Common.Common;
import com.khelfi.snackdemo.Model.Request;
import com.khelfi.snackdemo.OrderActivity;
import com.khelfi.snackdemo.R;

public class ListenOrderService extends Service implements ChildEventListener {

    DatabaseReference request_table;
    public static String userPhone;

    public ListenOrderService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        request_table = FirebaseDatabase.getInstance().getReference("Requests");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        request_table.addChildEventListener(this);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    //ChildEventListener Interface

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        //Trigger here..
        Request request = dataSnapshot.getValue(Request.class);

        //TODO : Receive notification only when we're concerned
        if(request.getPhone() != userPhone)
            return;                        // <-- We're not concerned.

        showNotification(dataSnapshot.getKey(), request);
    }

    private void showNotification(String key, Request request) {
        Intent intent = new Intent(getBaseContext(), OrderActivity.class);
        intent.putExtra("userPhone", request.getPhone());

        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);   // Because our service will work in the background --> https://developer.android.com/reference/android/app/PendingIntent.html

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getBaseContext());

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("Snack Demo")
                .setContentInfo("Your order has been updated")
                .setContentText("Order #" + key + " is " + OrderActivity.codeToStatus(request.getStatus()))
                .setContentIntent(pendingIntent)
                .setContentInfo("info")
                .setSmallIcon(R.mipmap.ic_burger);

        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
