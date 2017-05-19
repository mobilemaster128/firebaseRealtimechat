package com.anonymous.messaging.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Developer on 4/25/2017.
 */

@IgnoreExtraProperties
public class Contact {
    public String uId;
    public String name;
    public String key;

    public Contact() {
    }

    public Contact(String uId, String name, String key) {
        this.uId = uId;
        this.name = name;
        this.key = key;
    }

    public Contact(Item item) {
        this.uId = item.uId;
        this.name = item.name;
        this.key = key;
    }
}
