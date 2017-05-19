package com.anonymous.messaging.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Developer on 4/25/2017.
 */

@IgnoreExtraProperties
public class Service {
    public String message;
    public String url;
    public long timestamp;

    public Service() {
    }

    public Service(String message, String url, long timestamp) {
        this.message = message;
        this.url = url;
        this.timestamp = timestamp;
    }

    public  Service(Item item) {
        this.message = item.message;
        this.url = item.name;
        this.timestamp = item.timestamp;
    }
}
