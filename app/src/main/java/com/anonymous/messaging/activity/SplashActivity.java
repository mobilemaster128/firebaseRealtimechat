package com.anonymous.messaging.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.anonymous.messaging.MessagingApp;
import com.anonymous.messaging.R;
import com.anonymous.messaging.core.login.LoginContract;
import com.anonymous.messaging.core.login.LoginPresenter;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.utils.Constants;
import com.anonymous.messaging.utils.SharedPrefUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity implements LoginContract.View {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int SPLASH_TIME_MS = 2000;
    private Handler mHandler;
    private Runnable mRunnable;
    private LoginPresenter mLoginPresenter;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mLoginPresenter = new LoginPresenter(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);
        mHandler = new Handler();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (checkPlayServices()) {
                    String token;
                    do {
                        token = FirebaseInstanceId.getInstance().getToken();
                        //token = new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null);
                    } while (token == null);
                    if (token == null) {
                        token = "";
                    }
                    mLoginPresenter.login(token);
                    mProgressDialog.show();
                }
            }
        };

        mHandler.postDelayed(mRunnable, SPLASH_TIME_MS);
    }

    @Override
    public void onLoginSuccess(User user) {
        FirebaseMessaging.getInstance().subscribeToTopic(MessagingApp.topicName);
        mProgressDialog.dismiss();
        //Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();
        MainActivity.startActivity(this, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }

    @Override
    public void onLoginFailure(String message) {
        mProgressDialog.dismiss();
        //Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("Google Play Service", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
