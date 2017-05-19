package com.anonymous.messaging.core.users;

import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.User;

import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface GetUsersContract {
    interface View {
        void onGetAllUsersSuccess(List<User> users);

        void onGetAllUsersFailure(String message);

        void onGetChatUsersSuccess(List<Contact> contacts);

        void onGetChatUsersFailure(String message);

        void onDeleteChatUsersSuccess(Contact contact);

        void onDeleteChatUsersFailure(String message);
    }

    interface Presenter {
        void getAllUsers();

        void getChatUsers(String uId);

        void deleteChatUsers(String uId, Contact contact);
    }

    interface Interactor {
        void getAllUsersFromFirebase();

        void getChatUsersFromFirebase(String uId);

        void deleteChatUsersFromFirebase(String uId, Contact contact);
    }

    interface OnGetAllUsersListener {
        void onGetAllUsersSuccess(List<User> users);

        void onGetAllUsersFailure(String message);
    }

    interface OnGetChatUsersListener {
        void onGetChatUsersSuccess(List<Contact> contacts);

        void onGetChatUsersFailure(String message);
    }

    interface OnDeleteChatUsersListener {
        void onDeleteChatUsersSuccess(Contact contact);

        void onDeleteChatUsersFailure(String message);
    }
}
