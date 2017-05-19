package com.anonymous.messaging.core.event;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.models.Whisper;
import com.anonymous.messaging.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

public class EventInteractor implements EventContract.Interactor {
    private static final String TAG = "ServiceInteractor";

    private EventContract.OnEventListener mOnEventListener;

    public EventInteractor(EventContract.OnEventListener onEventListener) {
        this.mOnEventListener = onEventListener;
    }

    @Override
    public void checkContacts(String uId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONTACTS).child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mOnEventListener.onCheckContacts(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void checkHears(String uId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_HEARS).child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mOnEventListener.onCheckHears(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void checkUsers(final String uId) {
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.ARG_USERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                mOnEventListener.onAddUser(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                mOnEventListener.onChangeUser(user);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mOnEventListener.onRemoveUser(user);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
