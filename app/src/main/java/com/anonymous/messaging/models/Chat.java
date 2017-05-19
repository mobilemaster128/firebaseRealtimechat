package com.anonymous.messaging.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Author: Kartik Sharma
 * Created on: 9/4/2016 , 12:43 PM
 * Project: FirebaseChat
 */

@IgnoreExtraProperties
public class Chat {
    public String uId;
    public String senderuId;
    public String message;
    public long timestamp;
    public boolean read;

    public Chat() {
    }

    public Chat(String uId, String senderuId, String message, long timestamp, boolean read) {
        this.uId = uId;
        this.senderuId = senderuId;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    public void read() {
        this.read = true;
    }

    public void unRead() {
        this.read = false;
    }

    public Chat(Item item) {
        this.uId = item.uId;
        this.senderuId = item.senderuId;
        this.message = item.message;
        this.timestamp = Long.MAX_VALUE - item.timestamp;
    }
}
