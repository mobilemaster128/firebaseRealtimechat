package com.anonymous.messaging.models;

import com.anonymous.messaging.utils.Constants;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Developer on 4/19/2017.
 */

@IgnoreExtraProperties
public class Item {
    public String uId;
    public String senderuId;
    public String message;
    public String key;
    public long timestamp;
    public int count;
    public String name;
    public String item_type;

    public Item() {
    }

    public void increase() {
        this.count++;
    }

    public void decrease() {
        if (this.count > 0) this.count--;
    }

    public void count(int count) {
        this.count = count;
    }

    public Item( String uId, String senderuId, String message, String key, String name, long timestamp, int count) {
        this.uId = uId;
        this.senderuId = senderuId;
        this.message = message;
        this.key = key;
        this.timestamp = timestamp;
        this.name = name;
        this.count = count;
        this.item_type = "ALL";
    }

    public Item(Say say, String type) {
        this.item_type = type;
        this.senderuId =  say.uId;
        this.message = say.message;
        this.timestamp = say.timestamp;
        this.name = Constants.NICK_NAME;
        this.count = 0;
    }

    public Item(Whisper whisper, String type) {
        this.item_type = type;
        this.senderuId =  whisper.uId;
        this.message = whisper.message;
        this.timestamp = whisper.timestamp;
        this.name = Constants.NICK_NAME;
        this.count = 0;
    }

    public Item(Chat chat, String name, String key) {
        this.item_type = Constants.CHAT_TYPE;
        this.uId = chat.uId;
        this.senderuId = chat.senderuId;
        this.message = chat.message;
        this.timestamp = chat.timestamp;
        this.name = name;
        this.key = key;
        this.count = 0;
    }

    public Item(Contact contact) {
        this.item_type = Constants.CONTACT_TYPE;
        this.uId = contact.uId;
        this.key = contact.key;
        this.name = contact.name;
        this.senderuId = "";
        this.message = "";
        this.timestamp = 0;
        this.count = 0;
    }

    public Item(Service service) {
        this.item_type = Constants.SERVICE_TYPE;
        this.message = service.message;
        this.timestamp = service.timestamp;
        this.count = 0;
    }

    public Item(Service service, boolean read) {
        this.item_type = Constants.SERVICE_TYPE;
        this.message = service.message;
        this.name = service.url;
        this.timestamp = service.timestamp;
        this.count = read ? 0 : 1;
    }
}
