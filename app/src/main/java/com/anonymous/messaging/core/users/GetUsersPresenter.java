package com.anonymous.messaging.core.users;

import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.User;

import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:05 PM
 * Project: FirebaseChat
 */

public class GetUsersPresenter implements GetUsersContract.Presenter, GetUsersContract.OnGetAllUsersListener, GetUsersContract.OnGetChatUsersListener, GetUsersContract.OnDeleteChatUsersListener {
    private GetUsersContract.View mView;
    private GetUsersInteractor mGetUsersInteractor;

    public GetUsersPresenter(GetUsersContract.View view) {
        this.mView = view;
        mGetUsersInteractor = new GetUsersInteractor(this, this, this);
    }

    @Override
    public void getAllUsers() {
        mGetUsersInteractor.getAllUsersFromFirebase();
    }

    @Override
    public void getChatUsers(String uId) {
        mGetUsersInteractor.getChatUsersFromFirebase(uId);
    }

    @Override
    public void deleteChatUsers(String uId, Contact contact) {
        mGetUsersInteractor.deleteChatUsersFromFirebase(uId, contact);
    }

    @Override
    public void onGetAllUsersSuccess(List<User> users) {
        mView.onGetAllUsersSuccess(users);
    }

    @Override
    public void onGetAllUsersFailure(String message) {
        mView.onGetAllUsersFailure(message);
    }

    @Override
    public void onGetChatUsersSuccess(List<Contact> contacts) {
        mView.onGetChatUsersSuccess(contacts);
    }

    @Override
    public void onGetChatUsersFailure(String message) {
        mView.onGetChatUsersFailure(message);
    }

    @Override
    public void onDeleteChatUsersSuccess(Contact contact) {
        mView.onDeleteChatUsersSuccess(contact);
    }

    @Override
    public void onDeleteChatUsersFailure(String message) {
        mView.onDeleteChatUsersFailure(message);
    }
}
