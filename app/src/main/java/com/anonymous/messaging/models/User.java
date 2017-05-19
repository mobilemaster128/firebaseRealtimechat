package com.anonymous.messaging.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Author: Kartik Sharma
 * Created on: 9/1/2016 , 8:35 PM
 * Project: FirebaseChat
 */

@IgnoreExtraProperties
public class User {
    public String uId;
    public String firebaseToken;
    public String password;
    public long timeStamp;
    public boolean online;

    public User() {
    }

    public User(String uId, String firebaseToken, String password, long timeStamp, boolean online) {
        this.uId = uId;
        this.firebaseToken = firebaseToken;
        this.password = password;
        this.timeStamp = timeStamp;
        this.online = online;
    }
}
