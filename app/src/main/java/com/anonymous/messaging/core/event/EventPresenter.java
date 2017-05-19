package com.anonymous.messaging.core.event;

import com.anonymous.messaging.models.User;
import com.anonymous.messaging.models.Whisper;

import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:05 PM
 * Project: FirebaseChat
 */

public class EventPresenter implements EventContract.Presenter, EventContract.OnEventListener {
    private EventContract.View mView;
    private EventInteractor mEventInteractor;

    public EventPresenter(EventContract.View view) {
        this.mView = view;
        mEventInteractor = new EventInteractor(this);
    }

    @Override
    public void checkContacts(String uId) {
        mEventInteractor.checkContacts(uId);
    }

    @Override
    public void checkHears(String uId) {
        mEventInteractor.checkHears(uId);
    }

    @Override
    public void checkUsers(String uId) {
        mEventInteractor.checkUsers(uId);
    }

    @Override
    public void onCheckContacts(long count) {
        mView.onCheckContacts(count);
    }

    @Override
    public void onCheckHears(long count) {
        mView.onCheckHears(count);
    }

    @Override
    public void onAddUser(User user) {
        mView.onAddUser(user);
    }

    @Override
    public void onChangeUser(User user) {
        mView.onChangeUser(user);
    }

    @Override
    public void onRemoveUser(User user) {
        mView.onRemoveUser(user);
    }
}