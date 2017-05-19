package com.anonymous.messaging.core.user;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.anonymous.messaging.R;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.utils.Constants;
import com.anonymous.messaging.utils.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:08 PM
 * Project: FirebaseChat
 */

public class UserInteractor implements UserContract.Interactor {
    private UserContract.OnUserDatabaseListener mOnUserDatabaseListener;

    public UserInteractor(UserContract.OnUserDatabaseListener onUserDatabaseListener) {
        this.mOnUserDatabaseListener = onUserDatabaseListener;
    }

    @Override
    public void getUserFromDatabase(final String uId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mOnUserDatabaseListener.onGetUserSuccess(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnUserDatabaseListener.onGetUserFailure(databaseError.getMessage());
            }
        });
    }

    @Override
    public void lockUser(final String uId, final String password) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                user.password = password;
                user.online = false;
                final User newUser = user;
                if (TextUtils.equals(user.password, password)) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_USERS)
                            .child(uId)
                            .setValue(newUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //mOnUserDatabaseListener.onLockUserSuccess(user.password);
                                    } else {
                                        mOnUserDatabaseListener.onLockUserFailure("Failed");
                                    }
                                }
                            });
                    mOnUserDatabaseListener.onLockUserSuccess(user.password);
                } else {
                    mOnUserDatabaseListener.onLockUserFailure("Failed");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnUserDatabaseListener.onLockUserFailure("Failed");
            }
        });
    }

    @Override
    public void unlockUser(final String uId, final String password) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (TextUtils.equals(user.password, password)) {
                    user.password = "";
                    user.timeStamp = System.currentTimeMillis();
                    user.online = true;
                    final User newUser = user;
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_USERS)
                            .child(uId)
                            .setValue(newUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //mOnUserDatabaseListener.onUnlockUserSuccess("Success");
                                    } else {
                                        mOnUserDatabaseListener.onUnlockUserFailure("Failed");
                                    }
                                }
                            });
                    mOnUserDatabaseListener.onUnlockUserSuccess("Success");
                } else {
                    mOnUserDatabaseListener.onUnlockUserFailure("Failed");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnUserDatabaseListener.onUnlockUserFailure("Failed");
            }
        });
    }

    @Override
    public void forgot(final String uId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.timeStamp < System.currentTimeMillis()) {
                    user.timeStamp = System.currentTimeMillis() + Constants.TIME_LIMIT;
                    final User newUser = user;
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_USERS)
                            .child(uId)
                            .setValue(newUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //mOnUserDatabaseListener.onForgotSuccess(newUser.timeStamp);
                                    } else {
                                        mOnUserDatabaseListener.onForgotFailure("Failed");
                                    }
                                }
                            });
                    mOnUserDatabaseListener.onForgotSuccess(newUser.timeStamp);
                } else {
                    mOnUserDatabaseListener.onForgotFailure("Failed");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnUserDatabaseListener.onForgotFailure("Failed");
            }
        });
    }
}
