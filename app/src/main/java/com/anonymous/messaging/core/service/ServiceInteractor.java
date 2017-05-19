package com.anonymous.messaging.core.service;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.anonymous.messaging.MessagingApp;
import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Service;
import com.anonymous.messaging.models.Whisper;
import com.anonymous.messaging.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:08 PM
 * Project: FirebaseChat
 */

public class ServiceInteractor implements ServiceContract.Interactor {
    private static final String TAG = "ServiceInteractor";

    private ServiceContract.OnServiceListener mOnServiceListener;

    public ServiceInteractor(ServiceContract.OnServiceListener onServiceListener) {
        this.mOnServiceListener = onServiceListener;
    }

    @Override
    public void getServiceFromFirebase() {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_SERVICES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Service service = dataSnapshot.getValue(Service.class);
                mOnServiceListener.onGetServiceSuccess(service);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnServiceListener.onGetServiceFailure("Unable to get service: " + databaseError.getMessage());
            }
        });
    }
}
