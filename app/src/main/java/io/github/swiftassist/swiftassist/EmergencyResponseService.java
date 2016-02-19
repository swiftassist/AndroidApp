package io.github.swiftassist.swiftassist;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class EmergencyResponseService extends Service {

    private Firebase  emergencyFirebaseRef = new Firebase("https://swiftassist.firebaseio.com/emergency/");
    private ValueEventListener handler;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot arg0) {
                postNotif(arg0.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        emergencyFirebaseRef.addValueEventListener(handler);
    }

    private void postNotif(String notifString) {
        // post notification
    }

}