package com.anonymous.messaging.core.hear;

import android.content.Context;

import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.models.Whisper;

import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:05 PM
 * Project: FirebaseChat
 */

public class HearPresenter implements HearContract.Presenter, HearContract.OnHearListener {
    private HearContract.GetView mGetView;
    private HearContract.SetView mSetView;
    private HearInteractor mHearInteractor;

    public HearPresenter(HearContract.GetView getView) {
        this.mGetView = getView;
        mHearInteractor = new HearInteractor(this);
    }

    public HearPresenter(HearContract.SetView setView) {
        this.mSetView = setView;
        mHearInteractor = new HearInteractor(this);
    }

    @Override
    public void getHear(String uId) {
        mHearInteractor.getHearFromFirebase(uId);
    }

    @Override
    public void addHear(String uId, Whisper whisper) {
        mHearInteractor.addHearToFirebase(uId, whisper);
    }

    @Override
    public void replyToHear(String uId, Whisper whisper, Chat chat, String name) { mHearInteractor.replyToHear(uId, whisper, chat, name); }

    @Override
    public void reportHear(String uId, Whisper whisper) {
        mHearInteractor.reportHear(uId, whisper);
    }

    @Override
    public void passHear(String uId, Whisper whisper) {
        mHearInteractor.deleteFromFirebaseHears(uId, whisper);
    }

    @Override
    public void onAddHear(Whisper whisper) {
        mGetView.onAddHear(whisper);
    }

    @Override
    public void onDeleteHear(Whisper whisper) {
        mGetView.onDeleteHear(whisper);
    }

    @Override
    public void onHearFailure(String message) {
        mGetView.onHearFailure(message);
    }

    @Override
    public void onAddHearSuccess(Whisper whisper) {
        mSetView.onAddHearSuccess(whisper);
    }

    @Override
    public void onAddHearFailure(String message) {
        mSetView.onAddHearFailure(message);
    }

    @Override
    public void onReplyToHearSuccess(Whisper whisper) {
        mSetView.onReplyToHearSuccess(whisper);
    }

    @Override
    public void onReplyToHearFailure(String message) {
        mSetView.onReplyToHearFailure(message);
    }

    @Override
    public void onDeleteHearSuccess(Whisper whisper) {
        mSetView.onDeleteHearSuccess(whisper);
    }

    @Override
    public void onDeleteHearFailure(String message) {
        mSetView.onDeleteHearFailure(message);
    }

    @Override
    public void onReportHearSuccess(Whisper whisper) {
        mSetView.onReportHearSuccess(whisper);
    }

    @Override
    public void onReportHearFailure(String message) {
        mSetView.onReportHearFailure(message);
    }
}
