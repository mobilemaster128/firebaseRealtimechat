package com.anonymous.messaging.core.chatlist;

import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.Whisper;


/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:05 PM
 * Project: FirebaseChat
 */

public class ChatListPresenter implements ChatListContract.Presenter, ChatListContract.OnChatListListener {
    private ChatListContract.View mView;
    private ChatListInteractor mChatListInteractor;

    public ChatListPresenter(ChatListContract.View view) {
        this.mView = view;
        mChatListInteractor = new ChatListInteractor(this);
    }

    @Override
    public void getChatList(String uId) {
        mChatListInteractor.getChatList(uId);
    }

    @Override
    public void deleteFromFirebaseChats(String uId, Item item) {
        mChatListInteractor.deleteFromFirebaseChats(uId, item);
    }

    @Override
    public void onGetChatSuccess(Chat chat, String key, String name) {
        mView.onGetChatSuccess(chat, key, name);
    }

    @Override
    public void onChatsDeleted(String key) {
        mView.onChatsDeleted(key);
    }

    @Override
    public void onGetChatFailure(String message) {
        mView.onGetChatFailure(message);
    }

    @Override
    public void onDeleteChatSuccess(Item item) {
        mView.onDeleteChatSuccess(item);
    }

    @Override
    public void onDeleteChatFailure(String message) {
        mView.onDeleteChatFailure(message);
    }
}
