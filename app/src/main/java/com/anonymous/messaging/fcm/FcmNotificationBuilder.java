package com.anonymous.messaging.fcm;

import android.util.Log;

import com.anonymous.messaging.MessagingApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Author: Kartik Sharma
 * Created on: 10/16/2016 , 1:53 PM
 * Project: FirebaseChat
 */

public class FcmNotificationBuilder {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "FcmNotificationBuilder";
    private static final String SERVER_API_KEY = "AAAAWRQs4fE:APA91bH1ncDPTd5hZ6rD_AGJHJlcMLNKCT0iYwTsUMW4KbTf_DDw2z47j0CtCkAMAzDLRceb_ivFCYerPskOHwU4_v63Q8yvZvmB3eAMkykMHhpeOqDeAjx77vVTNGOXE-R1dKSVdpUh";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_KEY = "key=" + SERVER_API_KEY;
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String TO_TOPIC = "/topics/";
    // json related keys
    private static final String KEY_TO = "to";
    private static final String KEY_NOTIFICATION = "notification";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TEXT = "text";
    private static final String KEY_DATA = "data";
    public static final String KEY_KEY = "key";
    public static final String KEY_UID = "uid";
    private static final String KEY_FCM_TOKEN = "fcm_token";

    private String mTitle;
    private String mMessage;
    private String mKey;
    private String mUid;
    private String mReceiverFirebaseToken;

    private FcmNotificationBuilder() {

    }

    public static FcmNotificationBuilder initialize() {
        return new FcmNotificationBuilder();
    }

    public FcmNotificationBuilder title(String title) {
        mTitle = title;
        return this;
    }

    public FcmNotificationBuilder message(String message) {
        mMessage = message;
        return this;
    }

    public FcmNotificationBuilder key(String key) {
        mKey = key;
        return this;
    }

    public FcmNotificationBuilder uid(String uid) {
        mUid = uid;
        return this;
    }

    public FcmNotificationBuilder receiverFirebaseToken(String receiverFirebaseToken) {
        mReceiverFirebaseToken = receiverFirebaseToken;
        return this;
    }

    public void send() {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .addHeader(AUTHORIZATION, AUTH_KEY)
                .url(FCM_URL)
                .post(requestBody)
                .build();

        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onGetAllUsersFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    public void sendToTopic() {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonTopicBody().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .addHeader(AUTHORIZATION, AUTH_KEY)
                .url(FCM_URL)
                .post(requestBody)
                .build();

        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onGetAllUsersFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    private JSONObject getValidJsonBody() throws JSONException {
        JSONObject jsonObjectBody = new JSONObject();
        jsonObjectBody.put(KEY_TO, mReceiverFirebaseToken);

        JSONObject jsonObjectData = new JSONObject();
        jsonObjectData.put(KEY_TITLE, mTitle);
        jsonObjectData.put(KEY_TEXT, mMessage);
        jsonObjectData.put(KEY_KEY, mKey);
        jsonObjectData.put(KEY_UID, mUid);
        jsonObjectBody.put(KEY_DATA, jsonObjectData);

        return jsonObjectBody;
    }

    private JSONObject getValidJsonTopicBody() throws JSONException {
        JSONObject jsonObjectBody = new JSONObject();
        jsonObjectBody.put(KEY_TO, TO_TOPIC + MessagingApp.topicName);

        JSONObject jsonObjectData = new JSONObject();
        jsonObjectData.put(KEY_TITLE, mTitle);
        jsonObjectData.put(KEY_TEXT, mMessage);
        jsonObjectData.put(KEY_KEY, mKey);
        jsonObjectData.put(KEY_UID, mUid);
        jsonObjectBody.put(KEY_DATA, jsonObjectData);

        return jsonObjectBody;
    }
}
