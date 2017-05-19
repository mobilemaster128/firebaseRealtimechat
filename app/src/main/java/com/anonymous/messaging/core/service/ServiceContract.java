package com.anonymous.messaging.core.service;

import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.Service;
import com.anonymous.messaging.models.Whisper;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface ServiceContract {
    interface View {
        void onGetServiceSuccess(Service service);
        void onGetServiceFailure(String message);
    }

    interface Presenter {
        void getService();
    }

    interface Interactor {
        void getServiceFromFirebase();
    }

    interface OnServiceListener {
        void onGetServiceSuccess(Service service);
        void onGetServiceFailure(String message);
    }
}
