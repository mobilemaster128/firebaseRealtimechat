package com.anonymous.messaging.core.chat;

import android.content.Context;

import com.anonymous.messaging.models.Chat;


/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:05 PM
 * Project: FirebaseChat
 */

public class ChatPresenter implements ChatContract.Presenter, ChatContract.OnMessageListener {
    private ChatContract.View mView;
    private ChatInteractor mChatInteractor;

    public ChatPresenter(ChatContract.View view) {
        this.mView = view;
        mChatInteractor = new ChatInteractor(this);
    }

    @Override
    public void sendMessage(Chat chat, String key) {
        mChatInteractor.sendMessageToFirebaseUser(chat, key);
    }

    @Override
    public void getMessage(String uId, String key) {
        mChatInteractor.getMessageFromFirebaseUser(uId, key);
    }

    @Override
    public void checkMessage(String uId, String key) {
        mChatInteractor.checkMessage(uId, key);
    }

    @Override
    public void onSendMessageSuccess(String message) {
        mView.onSendMessageSuccess(message);
    }

    @Override
    public void onSendMessageFailure(String message) {
        mView.onSendMessageFailure(message);
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        mView.onGetMessagesSuccess(chat);
    }

    @Override
    public void onChangeMessagesSuccess(Chat chat) {
        mView.onChangeMessagesSuccess(chat);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        mView.onGetMessagesFailure(message);
    }

    @Override
    public void onCheckMessageSuccess(long count) {
        mView.onCheckMessageSuccess(count);
    }

    @Override
    public void onCheckMessageFailure(String message) {
        mView.onCheckMessageFailure(message);
    }

}
