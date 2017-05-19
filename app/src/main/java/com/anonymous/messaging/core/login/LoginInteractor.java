package com.anonymous.messaging.core.login;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.anonymous.messaging.R;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.utils.Constants;
import com.anonymous.messaging.utils.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:10 AM
 * Project: FirebaseChat
 */

public class LoginInteractor implements LoginContract.Interactor {
    private LoginContract.OnLoginListener mOnLoginListener;

    public LoginInteractor(LoginContract.OnLoginListener onLoginListener) {
        this.mOnLoginListener = onLoginListener;
    }

    @Override
    public void performFirebaseLogin(final String token) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            addUserToDatabase(token, FirebaseAuth.getInstance().getCurrentUser());
        } else {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "performFirebaseLogin:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (task.isSuccessful()) {
                                addUserToDatabase(token, task.getResult().getUser());
                            } else {
                                mOnLoginListener.onFailure(task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    public void addUserToDatabase(final String token, final FirebaseUser firebaseUser) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    user = new User(firebaseUser.getUid(), token, "", System.currentTimeMillis(), true);
                } else {
                    user.firebaseToken = token;
                    user.online = true;
                    if (user.timeStamp < System.currentTimeMillis()) {
                        user.timeStamp = System.currentTimeMillis();
                    }
                }
                final User newUser = user;
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                database.child(Constants.ARG_USERS)
                        .child(firebaseUser.getUid())
                        .setValue(newUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //mOnLoginListener.onSuccess(newUser);
                                } else {
                                    mOnLoginListener.onFailure("Failed");
                                }
                            }
                        });
                mOnLoginListener.onSuccess(newUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnLoginListener.onFailure("Failed");
            }
        });
    }
}
