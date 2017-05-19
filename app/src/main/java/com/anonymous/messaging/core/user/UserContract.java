package com.anonymous.messaging.core.user;

import android.content.Context;

import com.anonymous.messaging.models.User;
import com.google.firebase.auth.FirebaseUser;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface UserContract {
    interface View {
        void onGetUserSuccess(User user);
        void onGetUserFailure(String message);
        void onLockUserSuccess(String password);
        void onLockUserFailure(String message);
        void onUnlockUserSuccess(String message);
        void onUnlockUserFailure(String message);
        void onForgotSuccess(long timeStamp);
        void onForgotFailure(String message);
    }

    interface Presenter {
        void getUser(String uId);
        void lockUser(String uId, String password);
        void unlockUser(String uId, String password);
        void forgot(String uId);
    }

    interface Interactor {
        void getUserFromDatabase(String uId);
        void lockUser(String uId, String password);
        void unlockUser(String uId, String password);
        void forgot(String uId);
    }

    interface OnUserDatabaseListener {
        void onGetUserSuccess(User user);
        void onGetUserFailure(String message);
        void onLockUserSuccess(String password);
        void onLockUserFailure(String message);
        void onUnlockUserSuccess(String message);
        void onUnlockUserFailure(String message);
        void onForgotSuccess(long timeStamp);
        void onForgotFailure(String message);
    }
}
