package com.anonymous.messaging.core.service;

import com.anonymous.messaging.models.Service;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:05 PM
 * Project: FirebaseChat
 */

public class ServicePresenter implements ServiceContract.Presenter, ServiceContract.OnServiceListener {
    private ServiceContract.View mView;
    private ServiceInteractor mServiceInteractor;

    public ServicePresenter(ServiceContract.View view) {
        this.mView = view;
        mServiceInteractor = new ServiceInteractor(this);
    }

    @Override
    public void getService() {
        mServiceInteractor.getServiceFromFirebase();
    }

    @Override
    public void onGetServiceSuccess(Service service) {
        mView.onGetServiceSuccess(service);
    }

    @Override
    public void onGetServiceFailure(String message) {
        mView.onGetServiceFailure(message);
    }

}
