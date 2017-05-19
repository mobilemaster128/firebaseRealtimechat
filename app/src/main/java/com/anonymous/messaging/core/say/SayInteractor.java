package com.anonymous.messaging.core.say;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.anonymous.messaging.fcm.FcmNotificationBuilder;
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

public class SayInteractor implements SayContract.Interactor {
    private static final String TAG = "ChatListInteractor";

    private SayContract.OnSayListener mSaysListener;
    public SayInteractor( SayContract.OnSayListener onSaysListener) {
        this.mSaysListener = onSaysListener;
    }

    @Override
    public void getSayFromFirebase(final String uId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_WHISPERS).orderByChild("uId")
                .equalTo(uId).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDatabase.getInstance()
                        .getReference().child(Constants.ARG_WHISPERS).orderByChild("uId")
                        .equalTo(uId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Say say = dataSnapshot.getValue(Say.class);
                        mSaysListener.onAddSay(say);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Say say = dataSnapshot.getValue(Say.class);
                        mSaysListener.onDeleteSay(say);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mSaysListener.onSayFailure("Unable to get say: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mSaysListener.onSayFailure("Unable to get say: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void addSayToFirebase(final Say say) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_WHISPERS)
                .child(String.valueOf(say.timestamp)).setValue(say)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mSaysListener.onAddSaySuccess(say);
                        } else {
                            mSaysListener.onAddSayFailure("Failed");
                        }
                    }
                });
    }

    @Override
    public void sendSayToFirebaseUsers(final Say say, final String uId) {
        // get token
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS)
                .child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                final String token = user.firebaseToken;
                FcmNotificationBuilder.initialize()
                        .title(Constants.WHISPER_NOTI)
                        .uid(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .key(String.valueOf(say.timestamp))
                        .message(say.message)
                        .receiverFirebaseToken(token)
                        .send();
                mSaysListener.onSendSaySuccess(say, uId);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                mSaysListener.onSendSayFailure("Unable to send message: " + databaseError.getMessage(), uId);
            }
        });

    }

    @Override
    public void deleteFromFirebaseSays(final String key) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.ARG_WHISPERS)
                .child(key)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mSaysListener.onDeleteSaySuccess(key);
                        } else {
                            mSaysListener.onDeleteSayFailure("Failed");
                        }
                    }
                });
    }

    @Override
    public void reportFromFirebaseSays(final Say say, String uId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(Constants.ARG_WHISPERS + '/' + String.valueOf(say.timestamp), null);
        childUpdates.put(Constants.ARG_REPORT + '/' + uId + '_' + String.valueOf(System.currentTimeMillis()), say);

        databaseReference.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mSaysListener.onReportSaySuccess(say);
                } else {
                    mSaysListener.onReportSayFailure("Unable to report say");
                }
            }
        });
    }

    private void sendPushNotificationToApp(Say say) {
        FcmNotificationBuilder.initialize()
                .title(Constants.WHISPER_NOTI)
                .uid(say.uId)
                .key(String.format("%d", say.timestamp))
                .message(say.message)
                .sendToTopic();
    }
}
