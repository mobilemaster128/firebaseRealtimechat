package com.anonymous.messaging.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.anonymous.messaging.R;
import com.anonymous.messaging.fragments.UsersFragment;

public class DirectMessageActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    public static void startIntent(Context context) {
        Intent intent = new Intent(context, DirectMessageActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);

        bindViews();
        init();
    }

    private void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void init() {
        // set the toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Contacts");
        getSupportActionBar().setSubtitle("Delete a contact by holding down");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set the login screen fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_content_users,
                UsersFragment.newInstance(UsersFragment.TYPE_CHATS),
                UsersFragment.class.getSimpleName());
        fragmentTransaction.commit();
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
}
