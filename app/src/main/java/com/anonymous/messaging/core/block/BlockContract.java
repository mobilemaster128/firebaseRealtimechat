package com.anonymous.messaging.core.block;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface BlockContract {
    interface View {
        void onBlockUserSuccess(String chatRoom);
        void onBlockUserFailure(String message);
        void onUnBlockUserSuccess(String chatRoom);
        void onUnBlockUserFailure(String message);
        void onBlockAdded(String chatRoom);
        void onBlockRemoved(String chatRoom);
        void onCheckFailure(String message);
    }

    interface Presenter {
        void blockUser(String chatRoom);
        void unBlockUser(String chatRoom);
        void checkBlocks(String chatRoom);
    }

    interface Interactor {
        void blockUser(String chatRoom);
        void unBlockUser(String chatRoom);
        void checkBlocks(String chatRoom);
    }

    interface OnContactDatabaseListener {
        void onBlockUserSuccess(String chatRoom);
        void onBlockUserFailure(String message);
        void onUnBlockUserSuccess(String chatRoom);
        void onUnBlockUserFailure(String message);
        void onBlockAdded(String chatRoom);
        void onBlockRemoved(String chatRoom);
        void onCheckFailure(String message);
    }
}
