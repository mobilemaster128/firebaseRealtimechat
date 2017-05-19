package com.anonymous.messaging;

import android.app.Application;
import android.util.Log;

import com.anonymous.messaging.models.Service;
import com.anonymous.messaging.utils.Constants;
import com.anonymous.messaging.utils.SharedPrefUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Developer on 4/19/2017.
 */

public class MessagingApp extends Application {
    public final static String topicName = "whisper";
    private static boolean sIsChatActivityOpen = false;
    private static boolean sIsMainActivityOpen = false;
    private static boolean firebaseInitialized = false;
    private String firebaseUrl = "https://iryl-yril.firebaseio.com/";
    private static MessagingApp instance = null;

    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        MessagingApp.sIsChatActivityOpen = isChatActivityOpen;
    }

    public static boolean isMainActivityOpen() {
        return sIsMainActivityOpen;
    }

    public static void setMainActivityOpen(boolean isMainActivityOpen) {
        MessagingApp.sIsMainActivityOpen = isMainActivityOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        try {
            if (!firebaseInitialized) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                //DatabaseReference myFirebaseRef = FirebaseDatabase.getInstance().getReference();
                //myFirebaseRef.keepSynced(true);
            }
            firebaseInitialized = true;
        } catch (Exception exception) {
            Log.d("Firebase", exception.toString());
            exception.printStackTrace();
        }
    }
    // Getter to access Singleton instance
    public static MessagingApp getInstance() {
        return instance ;
    }

    public String getService() {
        return new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_SERVICE);
    }

    public void setService(long timestamp) {
        new SharedPrefUtil(getApplicationContext()).saveString(Constants.ARG_SERVICE, String.valueOf(timestamp));
    }
}
