package com.anonymous.messaging.core.chatlist;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.anonymous.messaging.fcm.FcmNotificationBuilder;
import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.Item;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:08 PM
 * Project: FirebaseChat
 */

public class ChatListInteractor implements ChatListContract.Interactor {
    private static final String TAG = "ChatListInteractor";

    private ChatListContract.OnChatListListener mChatListListener;
    public ChatListInteractor(ChatListContract.OnChatListListener onChatListListener) {
        this.mChatListListener = onChatListListener;
    }

    @Override
    public void getChatList(final String uId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDatabase.getInstance()
                        .getReference().child(Constants.ARG_CHAT_ROOMS).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final String key = dataSnapshot.getKey();
                        if (TextUtils.equals(key.substring(0, 28), uId)) {
                            FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CHAT_ROOMS)
                                    .child(key).limitToLast(1).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    final Chat chat = dataSnapshot.getValue(Chat.class);
                                    mChatListListener.onGetChatSuccess(chat, key.substring(29), "");
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mChatListListener.onGetChatFailure("Unable to get chat: " + databaseError.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getKey().indexOf(uId) == 0) {
                            mChatListListener.onChatsDeleted(dataSnapshot.getKey().substring(29));
                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mChatListListener.onGetChatFailure("Unable to get chat: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mChatListListener.onGetChatFailure("Unable to get say: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void deleteFromFirebaseChats(final String uId, final Item item) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(Constants.ARG_CHAT_ROOMS + '/' + uId + "_" + item.key, null);
        if (item.name.isEmpty() || TextUtils.equals(item.name, Constants.NICK_NAME)) {
            childUpdates.put(Constants.ARG_CONTACTS + '/' + uId + '/' + uId + "_" + item.key, null);
        }

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mChatListListener.onDeleteChatSuccess(item);
                } else {
                    mChatListListener.onDeleteChatFailure("Unable to remove chat");
                }
            }
        });
    }

}
