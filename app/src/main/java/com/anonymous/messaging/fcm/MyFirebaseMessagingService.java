package com.anonymous.messaging.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.anonymous.messaging.activity.ChatActivity;
import com.anonymous.messaging.activity.MainActivity;
import com.anonymous.messaging.MessagingApp;
import com.anonymous.messaging.R;
import com.anonymous.messaging.events.PushNotificationEvent;
import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            // Check if message contains a data payload.
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            final String title = remoteMessage.getData().get(FcmNotificationBuilder.KEY_TITLE);
            final String message = remoteMessage.getData().get(FcmNotificationBuilder.KEY_TEXT);
            final String uId = remoteMessage.getData().get(FcmNotificationBuilder.KEY_UID);
            final String key = remoteMessage.getData().get(FcmNotificationBuilder.KEY_KEY);

            if (TextUtils.equals(uId, FirebaseAuth.getInstance().getCurrentUser().getUid())) return;

            if (TextUtils.equals(title, Constants.CHAT_NOTI)) {
                FirebaseDatabase.getInstance().getReference().child(Constants.ARG_CONTACTS).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Contact contact;
                        if (dataSnapshot.exists()) {
                            contact = dataSnapshot.getValue(Contact.class);
                        } else {
                            contact = new Contact(uId, Constants.NICK_NAME, key);
                        }
                        if (!MessagingApp.isChatActivityOpen() && !MessagingApp.isMainActivityOpen()) {
                            sendNotification(title,
                                    message,
                                    uId,
                                    contact.name,
                                    key);
                        } else {
                            EventBus.getDefault().post(new PushNotificationEvent(title,
                                    message,
                                    key,
                                    contact.name,
                                    uId));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (!MessagingApp.isChatActivityOpen() && !MessagingApp.isMainActivityOpen()) {
                            sendNotification(title,
                                    message,
                                    uId,
                                    Constants.NICK_NAME,
                                    key);
                        } else {
                            EventBus.getDefault().post(new PushNotificationEvent(title,
                                    message,
                                    key,
                                    Constants.NICK_NAME,
                                    uId));
                        }
                    }
                });
            //} else if (remoteMessage.getFrom().equals("/topics/" + MessagingApp.topicName) && !MessagingApp.isMainActivityOpen() && !MessagingApp.isChatActivityOpen()) {
            } else if (!MessagingApp.isMainActivityOpen() && !MessagingApp.isChatActivityOpen()) {
                sendNotification(title,
                        message,
                        uId,
                        Constants.NICK_NAME,
                        key);
            } else {
                EventBus.getDefault().post(new PushNotificationEvent(title,
                        message,
                        key,
                        Constants.NICK_NAME,
                        uId));
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String title,
                                  String message,
                                  String uId,
                                  String name,
                                  final String key) {
        Intent intent;
        if (TextUtils.equals(title, Constants.CHAT_NOTI)) {
            intent = new Intent(this, ChatActivity.class);
            intent.putExtra(Constants.ARG_NAME, name);
            intent.putExtra(Constants.ARG_RECEIVER_UID, uId);
            intent.putExtra(Constants.ARG_KEY, key);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.ARG_TITLE, title);
            intent.putExtra(Constants.ARG_MESSAGE, message);
            intent.putExtra(Constants.ARG_KEY, key);
            intent.putExtra(Constants.ARG_RECEIVER_UID, uId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_action_chat)
                .setContentTitle(title)
                .setContentText("From " + name + ":" + message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}