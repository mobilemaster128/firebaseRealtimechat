package com.anonymous.messaging.core.hear;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.anonymous.messaging.fcm.FcmNotificationBuilder;
import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.Say;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.models.Whisper;
import com.anonymous.messaging.utils.Constants;
import com.anonymous.messaging.utils.SharedPrefUtil;
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

public class HearInteractor implements HearContract.Interactor {
    private static final String TAG = "ServiceInteractor";

    private HearContract.OnHearListener mOnHearListener;

    public HearInteractor(HearContract.OnHearListener onHearListener) {
        this.mOnHearListener = onHearListener;
    }

    @Override
    public void getHearFromFirebase(final String uId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_HEARS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDatabase.getInstance().getReference().child(Constants.ARG_HEARS).child(uId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Whisper whisper = dataSnapshot.getValue(Whisper.class);
                        mOnHearListener.onAddHear(whisper);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Whisper whisper = dataSnapshot.getValue(Whisper.class);
                        mOnHearListener.onDeleteHear(whisper);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mOnHearListener.onHearFailure("Unable to get whisper: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnHearListener.onHearFailure("Unable to get whisper: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void addHearToFirebase(final String uId, final Whisper whisper) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_HEARS).child(uId).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 10) {
                    Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                    while (dataSnapshots.hasNext()) {
                        DataSnapshot dataSnapshotChild = dataSnapshots.next();
                        Whisper oldwhisper = dataSnapshotChild.getValue(Whisper.class);
                        if (TextUtils.equals(oldwhisper.uId, whisper.uId) && TextUtils.equals(oldwhisper.message, whisper.message)) {
                            mOnHearListener.onAddHearFailure("Same Whisper");
                            return;
                        }
                    }
                    databaseReference.child(Constants.ARG_HEARS).child(uId)
                            .child(String.valueOf(whisper.timestamp)).setValue(whisper)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mOnHearListener.onAddHearSuccess(whisper);
                                    } else {
                                        mOnHearListener.onAddHearFailure("Unable to save whisper");
                                    }
                                }
                            });
                } else {
                    mOnHearListener.onAddHearFailure("Your Hears over 10");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnHearListener.onAddHearFailure("Unable to save whisper: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void replyToHear(final String uId, final Whisper whisper, final Chat chat, final String name) {
        final String key = String.format("%d", System.currentTimeMillis());
        final String my_room = uId + "_" + key;
        final String his_room = whisper.uId + "_" + key;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        // get token
        databaseReference.child(Constants.ARG_USERS).getRef().child(whisper.uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                final String token = user.firebaseToken;
                // send request
                Chat oldchat = new Chat(uId,
                        whisper.uId,
                        whisper.message,
                        whisper.timestamp,
                        true);
                Contact contact = new Contact(whisper.uId, name, key);//Constants.NICK_NAME, key);
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(Constants.ARG_CHAT_ROOMS + '/' + my_room + '/' + String.format("%d", oldchat.timestamp), oldchat);
                childUpdates.put(Constants.ARG_CHAT_ROOMS + '/' + his_room + '/' + String.format("%d", oldchat.timestamp), oldchat);
                childUpdates.put(Constants.ARG_CHAT_ROOMS + '/' + my_room + '/' + String.format("%d", chat.timestamp), chat);
                childUpdates.put(Constants.ARG_CHAT_ROOMS + '/' + his_room + '/' + String.format("%d", chat.timestamp), chat);
                childUpdates.put(Constants.ARG_CONTACTS + '/' + uId + '/' + my_room, contact);
                childUpdates.put(Constants.ARG_HEARS + '/' + uId + '/' + String.format("%d", whisper.timestamp), null);

                databaseReference.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // send push notification to the receiver
                            FcmNotificationBuilder.initialize()
                                    .title(Constants.REPLY_NOTI)
                                    .uid(uId)
                                    .key(String.valueOf(whisper.timestamp))
                                    .message(chat.message)
                                    .receiverFirebaseToken(token)
                                    .send();
                            //mOnHearListener.onReplyToHearSuccess(whisper);
                            //databaseReference.child(Constants.ARG_WHISPERS).child(String.format("%d", item.timestamp)).removeValue();
                        } else {
                            mOnHearListener.onReplyToHearFailure("Unable to send message");
                        }
                    }
                });
                mOnHearListener.onReplyToHearSuccess(whisper);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnHearListener.onReplyToHearFailure("Unable to send message: " + databaseError.getMessage());
            }
        });
    }


    @Override
    public void reportHear(final String uId, final Whisper whisper) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.ARG_HEARS).child(uId)
                .child(String.valueOf(whisper.timestamp))
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // get token
                            FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).getRef().child(whisper.uId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    String token = user.firebaseToken;
                                    FcmNotificationBuilder.initialize()
                                            .title(Constants.REPORT_NOTI)
                                            .uid(uId)
                                            .key(String.valueOf(whisper.timestamp))
                                            .message(whisper.message)
                                            .receiverFirebaseToken(token)
                                            .send();
                                    mOnHearListener.onReportHearSuccess(whisper);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mOnHearListener.onReportHearFailure("Unable to send message: " + databaseError.getMessage());
                                }
                            });
                        } else {
                            mOnHearListener.onReportHearFailure("Failed");
                        }
                    }
                });
    }

    @Override
    public void deleteFromFirebaseHears(String uId, final Whisper whisper) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.ARG_HEARS).child(uId)
                .child(String.valueOf(whisper.timestamp))
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mOnHearListener.onDeleteHearSuccess(whisper);
                        } else {
                            mOnHearListener.onDeleteHearFailure("Failed");
                        }
                    }
                });
    }
}
