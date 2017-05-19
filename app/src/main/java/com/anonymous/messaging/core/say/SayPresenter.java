package com.anonymous.messaging.core.say;

import android.content.Context;

import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.Say;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.models.Whisper;

import java.util.List;


/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:05 PM
 * Project: FirebaseChat
 */

public class SayPresenter implements SayContract.Presenter, SayContract.OnSayListener {
    private SayContract.GetView mGetView;
    private SayContract.SetView mSetView;
    private SayInteractor mSayInteractor;

    public SayPresenter(SayContract.GetView getView) {
        this.mGetView = getView;
        mSayInteractor = new SayInteractor(this);
    }

    public SayPresenter(SayContract.SetView setView) {
        this.mSetView = setView;
        mSayInteractor = new SayInteractor(this);
    }

    @Override
    public void getSay(String uId) {
        mSayInteractor.getSayFromFirebase(uId);
    }

    @Override
    public void addSay(Say say) {
        mSayInteractor.addSayToFirebase(say);
    }

    @Override
    public void sendSay(Say say, String uId) {
        mSayInteractor.sendSayToFirebaseUsers(say, uId);
    }
    @Override
    public void deleteSay(String key) {
        mSayInteractor.deleteFromFirebaseSays(key);
    }

    @Override
    public void reportSay(Say say, String uId) {
        mSayInteractor.reportFromFirebaseSays(say, uId);
    }

    @Override
    public void onAddSay(Say say) {
        mGetView.onAddSay(say);
    }

    @Override
    public void onDeleteSay(Say say) {
        mGetView.onDeleteSay(say);
    }


    @Override
    public void onSayFailure(String message) {
        mGetView.onSayFailure(message);
    }

    @Override
    public void onAddSaySuccess(Say say) {
        mSetView.onAddSaySuccess(say);
    }

    @Override
    public void onAddSayFailure(String message) {
        mSetView.onAddSayFailure(message);
    }

    @Override
    public void onSendSaySuccess(Say say, String uId) {
        mSetView.onSendSaySuccess(say, uId);
    }

    @Override
    public void onSendSayFailure(String message, String uId) {
        mSetView.onSendSayFailure(message, uId);
    }

    @Override
    public void onDeleteSaySuccess(String key) {
        mSetView.onDeleteSaySuccess(key);
    }

    @Override
    public void onDeleteSayFailure(String message) {
        mSetView.onDeleteSayFailure(message);
    }

    @Override
    public void onReportSaySuccess(Say say) {
        mSetView.onReportSaySuccess(say);
    }

    @Override
    public void onReportSayFailure(String message) {
        mSetView.onReportSayFailure(message);
    }
}
