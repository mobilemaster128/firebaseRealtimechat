package com.anonymous.messaging.core.contact;

import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.User;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface ContactContract {
    interface View {
        void onGetContactSuccess(Contact contact);
        void onGetContactFailure(String message);
        void onSetContactSuccess(Contact contact);
        void onSetContactFailure(String message);
        void onContactAdded(Contact contact);
        void onContactChanged(Contact contact);
        void onContactDeleted(Contact contact);
        void onCheckUserSuccess(User user);
        void onCheckUserFailed(String message);
    }

    interface Presenter {
        void getContact(String uId, String key);
        void setContact(String uId, Contact contact);
        void checkContact(String uId);
        void checkUser(String uId);
    }

    interface Interactor {
        void getContactFromDatabase(String uId, String key);
        void setContactToDatabase(String uId, Contact contact);
        void checkContact(String uId);
        void checkUser(String uId);
    }

    interface OnContactDatabaseListener {
        void onGetContactSuccess(Contact contact);
        void onGetContactFailure(String message);
        void onSetContactSuccess(Contact contact);
        void onSetContactFailure(String message);
        void onContactAdded(Contact contact);
        void onContactChanged(Contact contact);
        void onContactDeleted(Contact contact);
        void onCheckUserSuccess(User user);
        void onCheckUserFailed(String message);
    }
}
