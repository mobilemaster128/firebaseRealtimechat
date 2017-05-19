package com.anonymous.messaging.core.contact;

import android.support.annotation.NonNull;

import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:08 PM
 * Project: FirebaseChat
 */

public class ContactInteractor implements ContactContract.Interactor {
    private ContactContract.OnContactDatabaseListener mOnContactDatabaseListener;

    public ContactInteractor(ContactContract.OnContactDatabaseListener onContactDatabaseListener) {
        this.mOnContactDatabaseListener = onContactDatabaseListener;
    }

    @Override
    public void getContactFromDatabase(final String uId, final String key) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONTACTS).child(uId).child(uId + "_" + key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    final Contact contact = new Contact(uId, "", key);//Constants.NICK_NAME, key);
                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONTACTS).child(uId).child(uId + "_" + key).setValue(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //mOnContactDatabaseListener.onGetContactSuccess(contact);
                            } else {
                                mOnContactDatabaseListener.onGetContactFailure("Unable to get contact");
                            }
                        }
                    });
                    mOnContactDatabaseListener.onGetContactSuccess(contact);
                } else {
                    Contact contact = dataSnapshot.getValue(Contact.class);
                    mOnContactDatabaseListener.onGetContactSuccess(contact);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnContactDatabaseListener.onGetContactFailure(databaseError.getMessage());
            }
        });
    }

    @Override
    public void checkContact(String uId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONTACTS)
                .child(uId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Contact contact = dataSnapshot.getValue(Contact.class);
                mOnContactDatabaseListener.onContactAdded(contact);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Contact contact = dataSnapshot.getValue(Contact.class);
                mOnContactDatabaseListener.onContactChanged(contact);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Contact contact = dataSnapshot.getValue(Contact.class);
                mOnContactDatabaseListener.onContactDeleted(contact);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnContactDatabaseListener.onGetContactFailure("Unable to get chat: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void checkUser(String uId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mOnContactDatabaseListener.onCheckUserSuccess(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnContactDatabaseListener.onCheckUserFailed(databaseError.getMessage());
            }
        });
    }

    @Override
    public void setContactToDatabase(String uId, final Contact contact) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.ARG_CONTACTS)
                .child(uId)
                .child(uId + "_" + contact.key)
                .setValue(contact)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //mOnContactDatabaseListener.onSetContactSuccess(contact);
                        } else {
                            mOnContactDatabaseListener.onSetContactFailure("Unable to save user" + contact.name);
                        }
                    }
                });
        mOnContactDatabaseListener.onSetContactSuccess(contact);
    }

}
