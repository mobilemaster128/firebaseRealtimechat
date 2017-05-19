package com.anonymous.messaging.core.contact;

import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.User;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:05 PM
 * Project: FirebaseChat
 */

public class ContactPresenter implements ContactContract.Presenter, ContactContract.OnContactDatabaseListener {
    private ContactContract.View mView;
    private ContactInteractor mContactInteractor;

    public ContactPresenter(ContactContract.View view) {
        this.mView = view;
        mContactInteractor = new ContactInteractor(this);
    }

    @Override
    public void getContact(String uId, String key) {
        mContactInteractor.getContactFromDatabase(uId, key);
    }

    @Override
    public void setContact(String uId, Contact contact) {
        mContactInteractor.setContactToDatabase(uId, contact);
    }

    @Override
    public void checkContact(String uId) {
        mContactInteractor.checkContact(uId);
    }

    @Override
    public void checkUser(String uId) {
        mContactInteractor.checkUser(uId);
    }

    @Override
    public void onGetContactSuccess(Contact contact) {
        mView.onGetContactSuccess(contact);
    }

    @Override
    public void onGetContactFailure(String message) {
        mView.onGetContactFailure(message);
    }

    @Override
    public void onSetContactSuccess(Contact contact) {
        mView.onSetContactSuccess(contact);
    }

    @Override
    public void onSetContactFailure(String message) {
        mView.onSetContactFailure(message);
    }

    @Override
    public void onContactAdded(Contact contact) {
        mView.onContactAdded(contact);
    }

    @Override
    public void onContactChanged(Contact contact) {
        mView.onContactChanged(contact);
    }

    @Override
    public void onContactDeleted(Contact contact) {
        mView.onContactDeleted(contact);
    }

    @Override
    public void onCheckUserSuccess(User user) {
        mView.onCheckUserSuccess(user);
    }

    @Override
    public void onCheckUserFailed(String message) {
        mView.onCheckUserFailed(message);
    }

}
