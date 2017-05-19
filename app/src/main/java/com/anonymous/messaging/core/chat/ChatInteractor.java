package com.anonymous.messaging.core.chat;

import android.content.Context;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.anonymous.messaging.MessagingApp;
import com.anonymous.messaging.fcm.FcmNotificationBuilder;
import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.utils.Constants;
import com.anonymous.messaging.utils.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:08 PM
 * Project: FirebaseChat
 */

public class ChatInteractor implements ChatContract.Interactor {
    private static final String TAG = "ChatInteractor";

    private ChatContract.OnMessageListener mOnMessageListener;

    public ChatInteractor(ChatContract.OnMessageListener onMessageListener) {
        this.mOnMessageListener = onMessageListener;
    }

    @Override
    public void sendMessageToFirebaseUser(final Chat chat, final String key) {
        final String his_room = chat.uId + "_" + key;
        final String my_room = chat.senderuId + "_" + key;

        // get token
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).getRef().child(chat.uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                final String token = user.firebaseToken;
                //start chat
                FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(Constants.ARG_CHAT_ROOMS + '/' + his_room + '/' + String.format("%d", chat.timestamp), chat);
                        childUpdates.put(Constants.ARG_CHAT_ROOMS + '/' + my_room + '/' + String.format("%d", chat.timestamp), chat);

                        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // send push notification to the receiver
                                    sendPushNotificationToReceiver(key,
                                            chat.message,
                                            chat.senderuId,
                                            token);
                                    //mOnMessageListener.onSendMessageSuccess("Success");
                                } else {
                                    mOnMessageListener.onSendMessageFailure("Unable to send message");
                                }
                            }
                        });
                        mOnMessageListener.onSendMessageSuccess("Success");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mOnMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
            }
        });
    }

    private void sendPushNotificationToReceiver(String key,
                                                String message,
                                                String uid,
                                                String receiverFirebaseToken) {
        FcmNotificationBuilder.initialize()
                .title(Constants.CHAT_NOTI)
                .uid(uid)
                .key(key)
                .message(message)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
    }

    @Override
    public void checkMessage(String uId, String key) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CHAT_ROOMS).child(uId + "_" + key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mOnMessageListener.onCheckMessageSuccess(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnMessageListener.onCheckMessageFailure("Unable to get message: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void getMessageFromFirebaseUser(final String uId, final String key) {
        final String room = uId + "_" + key;

        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e(TAG, "getMessageFromFirebaseUser");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (MessagingApp.isChatActivityOpen()) {
                                final Chat chat = dataSnapshot.getValue(Chat.class);
                                if (TextUtils.equals(uId, chat.uId) && !chat.read) { // other chat
                                    // set read
                                    chat.read();
                                    Map<String, Object> childUpdates = new HashMap<>();
                                    childUpdates.put(Constants.ARG_CHAT_ROOMS + '/' + uId + "_" + key + '/' + dataSnapshot.getKey(), chat);
                                    childUpdates.put(Constants.ARG_CHAT_ROOMS + '/' + chat.senderuId + "_" + key + '/' + dataSnapshot.getKey(), chat);

                                    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //mOnMessageListener.onGetMessagesSuccess(chat);
                                            } else {
                                                chat.unRead();
                                                mOnMessageListener.onGetMessagesSuccess(chat);
                                            }
                                        }
                                    });
                                    mOnMessageListener.onGetMessagesSuccess(chat);
                                } else {
                                    mOnMessageListener.onGetMessagesSuccess(chat);
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            final Chat chat = dataSnapshot.getValue(Chat.class);
                            if (TextUtils.equals(uId, chat.senderuId)) { // my chat
                                mOnMessageListener.onChangeMessagesSuccess(chat);
                            }
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mOnMessageListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnMessageListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        });
    }

}
