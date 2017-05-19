package com.anonymous.messaging.core.login;

import android.app.Activity;

import com.anonymous.messaging.models.User;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:10 AM
 * Project: FirebaseChat
 */

public class LoginPresenter implements LoginContract.Presenter, LoginContract.OnLoginListener {
    private LoginContract.View mLoginView;
    private LoginInteractor mLoginInteractor;

    public LoginPresenter(LoginContract.View loginView) {
        this.mLoginView = loginView;
        mLoginInteractor = new LoginInteractor(this);
    }

    @Override
    public void login(String token) {
        mLoginInteractor.performFirebaseLogin(token);
    }

    @Override
    public void onSuccess(User user) {
        mLoginView.onLoginSuccess(user);
    }

    @Override
    public void onFailure(String message) {
        mLoginView.onLoginFailure(message);
    }
}
