package com.anonymous.messaging.events;

/**
 * Author: Kartik Sharma
 * Created on: 10/18/2016 , 10:16 PM
 * Project: FirebaseChat
 */

public class PushNotificationEvent {
    private String title;
    private String message;
    private String key;
    private String name;
    private String uid;

    public PushNotificationEvent() {
    }

    public PushNotificationEvent(String title, String message, String key, String name, String uid) {
        this.title = title;
        this.message = message;
        this.key = key;
        this.name = name;
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
