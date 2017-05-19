package com.anonymous.messaging.core.users;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:08 PM
 * Project: FirebaseChat
 */

public class GetUsersInteractor implements GetUsersContract.Interactor {
    private static final String TAG = "ServiceInteractor";

    private GetUsersContract.OnGetAllUsersListener mOnGetAllUsersListener;
    private GetUsersContract.OnGetChatUsersListener mOnGetChatUsersListener;
    private GetUsersContract.OnDeleteChatUsersListener mOnDeleteChatUsersListener;

    public GetUsersInteractor(GetUsersContract.OnGetAllUsersListener onGetAllUsersListener, GetUsersContract.OnGetChatUsersListener OnGetChatUsersListener, GetUsersContract.OnDeleteChatUsersListener onDeleteChatUsersListener) {
        this.mOnGetAllUsersListener = onGetAllUsersListener;
        this.mOnGetChatUsersListener = OnGetChatUsersListener;
        this.mOnDeleteChatUsersListener = onDeleteChatUsersListener;
    }

    @Override
    public void getAllUsersFromFirebase() {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                List<User> users = new ArrayList<>();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    User user = dataSnapshotChild.getValue(User.class);
                    if (!TextUtils.equals(user.uId, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        users.add(user);
                    }
                }
                mOnGetAllUsersListener.onGetAllUsersSuccess(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetAllUsersListener.onGetAllUsersFailure(databaseError.getMessage());
            }
        });
    }

    @Override
    public void getChatUsersFromFirebase(final String uId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONTACTS).child(uId).orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                List<Contact> contacts = new ArrayList<>();
                while (dataSnapshots.hasNext()){
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    Contact contact = dataSnapshotChild.getValue(Contact.class);
                    if (!contact.name.isEmpty() && !TextUtils.equals(contact.name, Constants.NICK_NAME)) {
                        contacts.add(contact);
                    }
                }
                mOnGetChatUsersListener.onGetChatUsersSuccess(contacts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetChatUsersListener.onGetChatUsersFailure(databaseError.getMessage());
            }
        });
    }

    @Override
    public void deleteChatUsersFromFirebase(String uId, final Contact contact) {

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(Constants.ARG_CHAT_ROOMS + '/' + uId + "_" + contact.key, null);
        childUpdates.put(Constants.ARG_CONTACTS + '/' + uId + '/' + uId + "_" + contact.key, null);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //mOnDeleteChatUsersListener.onDeleteChatUsersSuccess(contact);
                } else {
                    mOnDeleteChatUsersListener.onDeleteChatUsersFailure("Unable to remove contact");
                }
            }
        });
        mOnDeleteChatUsersListener.onDeleteChatUsersSuccess(contact);
    }
}
