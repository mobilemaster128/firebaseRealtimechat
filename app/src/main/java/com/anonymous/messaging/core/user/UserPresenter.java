package com.anonymous.messaging.core.user;

import android.content.Context;

import com.anonymous.messaging.models.User;
import com.google.firebase.auth.FirebaseUser;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:05 PM
 * Project: FirebaseChat
 */

public class UserPresenter implements UserContract.Presenter, UserContract.OnUserDatabaseListener {
    private UserContract.View mView;
    private UserInteractor mUserInteractor;

    public UserPresenter(UserContract.View view) {
        this.mView = view;
        mUserInteractor = new UserInteractor(this);
    }

    @Override
    public void getUser(String uId) {
        mUserInteractor.getUserFromDatabase(uId);
    }

    @Override
    public void lockUser(String uId, String password) {
        mUserInteractor.lockUser(uId, password);
    }

    @Override
    public void unlockUser(String uId, String password) {
        mUserInteractor.unlockUser(uId, password);
    }

    @Override
    public void forgot(String uId) {
        mUserInteractor.forgot(uId);
    }

    @Override
    public void onGetUserSuccess(User user) {
        mView.onGetUserSuccess(user);
    }

    @Override
    public void onGetUserFailure(String message) {
        mView.onGetUserFailure(message);
    }

    @Override
    public void onLockUserSuccess(String message) {
        mView.onLockUserSuccess(message);
    }

    @Override
    public void onLockUserFailure(String message) {
        mView.onLockUserFailure(message);
    }

    @Override
    public void onUnlockUserSuccess(String message) {
        mView.onUnlockUserSuccess(message);
    }

    @Override
    public void onUnlockUserFailure(String message) {
        mView.onUnlockUserFailure(message);
    }

    @Override
    public void onForgotSuccess(long timeStamp) {
        mView.onForgotSuccess(timeStamp);
    }

    @Override
    public void onForgotFailure(String message) {
        mView.onForgotFailure(message);
    }
}
