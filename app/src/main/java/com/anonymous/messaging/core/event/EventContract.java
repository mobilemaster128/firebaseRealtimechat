package com.anonymous.messaging.core.event;

import com.anonymous.messaging.models.User;
import com.anonymous.messaging.models.Whisper;

import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface EventContract {
    interface View {
        void onCheckContacts(long count);
        void onCheckHears(long count);
        void onAddUser(User user);
        void onChangeUser(User user);
        void onRemoveUser(User user);
    }

    interface Presenter {
        void checkHears(String uId);
        void checkContacts(String uId);
        void checkUsers(String uId);
    }

    interface Interactor {
        void checkHears(String uId);
        void checkContacts(String uId);
        void checkUsers(String uId);
    }

    interface OnEventListener {
        void onCheckContacts(long count);
        void onCheckHears(long count);
        void onAddUser(User user);
        void onChangeUser(User user);
        void onRemoveUser(User user);
    }
}
