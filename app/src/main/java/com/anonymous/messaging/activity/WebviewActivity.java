package com.anonymous.messaging.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.anonymous.messaging.R;
import com.anonymous.messaging.utils.Constants;

public class WebviewActivity extends AppCompatActivity {
    WebView webView;
    Bundle webviewBundle;
    private String mUrl;
    private Toolbar mToolbar;
    private ProgressDialog mProgressDialog;
    int state = 0;

    public static void startActivity(Context context,
                                     String url) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra(Constants.ARG_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Service");
        //getSupportActionBar().setSubtitle("");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUrl = getIntent().getExtras().getString(Constants.ARG_URL);

        webView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        MyWebViewClient webViewClient = new MyWebViewClient();

        webView.clearCache(true);
        webView.clearHistory();

        webView.setWebViewClient(webViewClient);

        if (checkNetworkConnection()) {
            webView.loadUrl(mUrl);
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Please check your network connection.");
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( (keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack() ) {
            webView.restoreState(webviewBundle);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            findViewById(R.id.webview).setVisibility(View.VISIBLE);
            mProgressDialog.dismiss();
            Handler handler = new Handler();

            if (state == 0) {
                webviewBundle = new Bundle();
                webView.saveState(webviewBundle);
                state = 1;
            }
        }
    }
}
