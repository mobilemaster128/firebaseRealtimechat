package com.anonymous.messaging.core.chat;

import android.content.Context;

import com.anonymous.messaging.models.Chat;


/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface ChatContract {
    interface View {
        void onSendMessageSuccess(String message);
        void onSendMessageFailure(String message);
        void onGetMessagesSuccess(Chat chat);
        void onChangeMessagesSuccess(Chat chat);
        void onGetMessagesFailure(String message);
        void onCheckMessageSuccess(long count);
        void onCheckMessageFailure(String message);
    }

    interface Presenter {
        void sendMessage(Chat chat, String key);
        void getMessage(String uId, String key);
        void checkMessage(String uId, String key);
    }

    interface Interactor {
        void sendMessageToFirebaseUser(Chat chat, String key);
        void getMessageFromFirebaseUser(String uId, String key);
        void checkMessage(String uId, String key);
    }

    interface OnMessageListener {
        void onSendMessageSuccess(String message);

        void onSendMessageFailure(String message);

        void onGetMessagesSuccess(Chat chat);

        void onChangeMessagesSuccess(Chat chat);

        void onGetMessagesFailure(String message);
        void onCheckMessageSuccess(long count);
        void onCheckMessageFailure(String message);
    }

}
