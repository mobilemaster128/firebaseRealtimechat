package com.anonymous.messaging.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Developer on 4/19/2017.
 */

@IgnoreExtraProperties
public class Whisper {
    public String uId;
    public String message;
    public long timestamp;

    public Whisper() {
    }

    public Whisper(String uId, String message, long timestamp) {
        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public  Whisper(Item item) {
        this.uId = item.senderuId;
        this.message = item.message;
        this.timestamp = item.timestamp;
    }
}
