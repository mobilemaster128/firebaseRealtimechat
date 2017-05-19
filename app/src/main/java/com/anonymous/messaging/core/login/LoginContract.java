package com.anonymous.messaging.core.login;

import android.app.Activity;

import com.anonymous.messaging.models.User;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface LoginContract {
    interface View {
        void onLoginSuccess(User user);
        void onLoginFailure(String message);
    }

    interface Presenter {
        void login(String token);
    }

    interface Interactor {
        void performFirebaseLogin(String token);
    }

    interface OnLoginListener {
        void onSuccess(User user);

        void onFailure(String message);
    }
}
