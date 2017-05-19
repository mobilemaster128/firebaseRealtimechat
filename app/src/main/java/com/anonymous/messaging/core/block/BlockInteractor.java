package com.anonymous.messaging.core.block;

import android.support.annotation.NonNull;

import com.anonymous.messaging.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:08 PM
 * Project: FirebaseChat
 */

public class BlockInteractor implements BlockContract.Interactor {
    private BlockContract.OnContactDatabaseListener mOnContactDatabaseListener;

    public BlockInteractor(BlockContract.OnContactDatabaseListener onContactDatabaseListener) {
        this.mOnContactDatabaseListener = onContactDatabaseListener;
    }

    @Override
    public void blockUser(final String chatRoom) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.ARG_BLOCKS)
                .child(chatRoom)
                .setValue(String.valueOf(System.currentTimeMillis()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mOnContactDatabaseListener.onBlockUserSuccess(chatRoom);
                        } else {
                            mOnContactDatabaseListener.onBlockUserFailure("Unable to block user");
                        }
                    }
                });
    }

    @Override
    public void unBlockUser(final String chatRoom) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.ARG_BLOCKS)
                .child(chatRoom)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mOnContactDatabaseListener.onUnBlockUserSuccess(chatRoom);
                        } else {
                            mOnContactDatabaseListener.onUnBlockUserFailure("Unable to block user");
                        }
                    }
                });
    }

    @Override
    public void checkBlocks(String chatRoom) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_BLOCKS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mOnContactDatabaseListener.onBlockAdded(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mOnContactDatabaseListener.onBlockRemoved(dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnContactDatabaseListener.onCheckFailure("Unable to get chat: " + databaseError.getMessage());
            }
        });
    }
}
