package com.anonymous.messaging.core.hear;

import android.content.Context;

import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.Say;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.models.Whisper;

import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface HearContract {
    interface GetView {
        void onAddHear(Whisper whisper);

        void onDeleteHear(Whisper whisper);

        void onHearFailure(String message);
    }

    interface SetView {
        void onAddHearSuccess(Whisper whisper);
        void onAddHearFailure(String message);
        void onReplyToHearSuccess(Whisper whisper);
        void onReplyToHearFailure(String message);
        void onDeleteHearSuccess(Whisper whisper);
        void onDeleteHearFailure(String message);
        void onReportHearSuccess(Whisper whisper);
        void onReportHearFailure(String message);
    }

    interface Presenter {
        void getHear(String uId);
        void replyToHear(String uId, Whisper whisper, Chat chat, String name);
        void addHear(String uId, Whisper whisper);
        void reportHear(String uId, Whisper whisper);
        void passHear(String uId, Whisper whisper);
    }

    interface Interactor {
        void getHearFromFirebase(String uId);
        void replyToHear(String uId, Whisper whisper, Chat chat, String name);
        void addHearToFirebase(String uId, Whisper whisper);
        void reportHear(String uId, Whisper whisper);
        void deleteFromFirebaseHears(String uId, Whisper whisper);
    }

    interface OnHearListener {
        void onAddHear(Whisper whisper);

        void onDeleteHear(Whisper whisper);

        void onHearFailure(String message);
        void onAddHearSuccess(Whisper whisper);
        void onAddHearFailure(String message);
        void onReplyToHearSuccess(Whisper whisper);
        void onReplyToHearFailure(String message);
        void onDeleteHearSuccess(Whisper whisper);
        void onDeleteHearFailure(String message);
        void onReportHearSuccess(Whisper whisper);
        void onReportHearFailure(String message);
    }
}
