package com.anonymous.messaging.core.say;

import android.content.Context;

import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.Say;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.models.Whisper;

import java.util.List;


/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface SayContract {
    interface GetView {
        void onAddSay(Say say);

        void onDeleteSay(Say say);

        void onSayFailure(String message);
    }

    interface SetView {
        void onAddSaySuccess(Say say);
        void onAddSayFailure(String message);
        void onSendSaySuccess(Say say, String uId);
        void onSendSayFailure(String message, String uId);
        void onDeleteSaySuccess(String key);
        void onDeleteSayFailure(String message);
        void onReportSaySuccess(Say say);
        void onReportSayFailure(String message);
    }

    interface Presenter {
        void getSay(String uId);
        void addSay(Say say);
        void sendSay(Say say, String uId);
        void deleteSay(String key);
        void reportSay(Say say, String uId);
    }

    interface Interactor {
        void getSayFromFirebase(String uId);
        void addSayToFirebase(Say say);
        void sendSayToFirebaseUsers(Say say, String uId);
        void deleteFromFirebaseSays(String key);
        void reportFromFirebaseSays(Say say, String uId);
    }

    interface OnSayListener {
        void onAddSay(Say say);
        void onDeleteSay(Say say);
        void onSayFailure(String message);
        void onAddSaySuccess(Say say);
        void onAddSayFailure(String message);
        void onSendSaySuccess(Say say, String uId);
        void onSendSayFailure(String message, String uId);
        void onDeleteSaySuccess(String key);
        void onDeleteSayFailure(String message);
        void onReportSaySuccess(Say say);
        void onReportSayFailure(String message);
    }
}
