package com.anonymous.messaging.core.chatlist;

import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.Whisper;


/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface ChatListContract {
    interface View {
        void onGetChatSuccess(Chat chat, String key, String name);
        void onChatsDeleted(String key);
        void onGetChatFailure(String message);
        void onDeleteChatSuccess(Item item);
        void onDeleteChatFailure(String message);
    }

    interface Presenter {
        void getChatList(String uId);
        void deleteFromFirebaseChats(String uId, Item item);
    }

    interface Interactor {
        void getChatList(String uId);
        void deleteFromFirebaseChats(String uId, Item item);
    }

    interface OnChatListListener {
        void onGetChatSuccess(Chat chat, String key,  String name);
        void onChatsDeleted(String key);
        void onGetChatFailure(String message);
        void onDeleteChatSuccess(Item item);
        void onDeleteChatFailure(String message);;
    }
}
