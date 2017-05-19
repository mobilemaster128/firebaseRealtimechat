package com.anonymous.messaging.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Developer on 4/28/2017.
 */

@IgnoreExtraProperties
public class Say {
    public String uId;
    public String message;
    public long timestamp;

    public Say() {
    }

    public Say(String uId, String message, long timestamp) {
        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public  Say(Item item) {
        this.uId = item.senderuId;
        this.message = item.message;
        this.timestamp = item.timestamp;
    }
}
