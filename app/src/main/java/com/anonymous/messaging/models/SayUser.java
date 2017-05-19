package com.anonymous.messaging.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Developer on 4/30/2017.
 */

@IgnoreExtraProperties
public class SayUser {
    public String uId;
    public String firebaseToken;
    public long timeStamp;
    public boolean online;
    public boolean sent;

    public SayUser() {
    }

    public SayUser(User user, boolean sent) {
        this.uId = user.uId;
        this.firebaseToken = user.firebaseToken;
        this.timeStamp = user.timeStamp;
        this.online = user.online;
        this.sent = sent;
    }
}
