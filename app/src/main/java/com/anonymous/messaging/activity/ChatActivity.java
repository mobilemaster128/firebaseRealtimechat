package com.anonymous.messaging.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.anonymous.messaging.MessagingApp;
import com.anonymous.messaging.R;
import com.anonymous.messaging.core.contact.ContactContract;
import com.anonymous.messaging.core.contact.ContactPresenter;
import com.anonymous.messaging.fragments.ChatFragment;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChatActivity extends AppCompatActivity implements ContactContract.View {
    private Toolbar mToolbar;
    private ContactPresenter mContactPresent;
    private String chatname;
    private User mPartner;
    private ChatFragment mChatFragment;
    private Menu menu;

    private boolean mMyBlocked = false;
    private boolean mHisBlocked = false;

    public static void startActivity(Context context,
                                     String name,
                                     String receiverUid,
                                     String key) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.ARG_NAME, name);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_KEY, key);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bindViews();
        init();
    }

    private void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void init() {
        // set the toolbar
        setSupportActionBar(mToolbar);
        // set toolbar title
        setTitle(Constants.ARG_NAME);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContactPresent = new ContactPresenter(this);

        // set the register screen fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mChatFragment = ChatFragment.newInstance(getIntent().getExtras().getString(Constants.ARG_NAME),
                getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID),
                getIntent().getExtras().getString(Constants.ARG_KEY));
        fragmentTransaction.replace(R.id.frame_layout_content_chat, mChatFragment);
                ChatFragment.class.getSimpleName();
        fragmentTransaction.commit();
        mContactPresent.getContact(FirebaseAuth.getInstance().getCurrentUser().getUid(), getIntent().getExtras().getString(Constants.ARG_KEY));
        mContactPresent.checkUser(getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID));
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        this.menu = menu;
        if (mMyBlocked)
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_unblock));
        else menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_block));
        return true;
    }

    public void setMyBlocked(boolean blocked) {
        mMyBlocked = blocked;
        if (menu != null) {
            if (blocked)
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_unblock));
            else menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_block));
        }
    }

    public void setHisBlocked(boolean blocked) {
        mHisBlocked = blocked;
        setSubTitle();
    }

    private void setSubTitle() {
        if (mHisBlocked) {
            getSupportActionBar().setSubtitle("Blocked");
            return;
        }
        if (mPartner == null) {
            getSupportActionBar().setSubtitle("Error");
        } else {
            long today = (getStartOfDay(new Date()).getTime() - mPartner.timeStamp)/(24*60*60*1000);
            if (today < 1) {
                if (mPartner.online) {
                    getSupportActionBar().setSubtitle("Online");
                } else {
                    //SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
                    getSupportActionBar().setSubtitle("Today");//(formatter.format(last));
                }
            } else if (today < 2) {
                getSupportActionBar().setSubtitle("Yesterday");
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
                getSupportActionBar().setSubtitle(formatter.format(mPartner.timeStamp));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_block:
                if (mMyBlocked) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                    //dialogBuilder.setTitle("");
                    dialogBuilder.setMessage("Do you want to unblock this chat?");
                    dialogBuilder.setPositiveButton("UnBlock", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mChatFragment.unBlock();
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    dialogBuilder.show();
                    //mChatFragment.unBlock();
                }
                else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                    //dialogBuilder.setTitle("");
                    dialogBuilder.setMessage("Do you want to block this chat?");
                    dialogBuilder.setPositiveButton("Block", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mChatFragment.block();
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    dialogBuilder.show();
                    //mChatFragment.block();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTitle(String title) {
        chatname = title;
        if (title.isEmpty() || title == null) {
            title = "Add " + Constants.NICK_NAME;
        } else if (TextUtils.equals(title, Constants.NICK_NAME)) {
            title = "Add " + title;
        }
        getSupportActionBar().setTitle(title);
    }

    public void changeName() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        dialogBuilder.setTitle("Input Name");// Set up the input
        final EditText nameEdit = new EditText(this);
        nameEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        nameEdit.setHint("name");
        nameEdit.setText(chatname);
        nameEdit.setTextColor(Color.WHITE);
        nameEdit.setHintTextColor(Color.GRAY);
        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setPadding(24, 24, 24, 24);
        view.addView(nameEdit);
        dialogBuilder.setView(view);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = nameEdit.getText().toString();
                //if (name.isEmpty()) {
                    //name = Constants.NICK_NAME;
                //}
                Contact contact = new Contact(getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID),
                        name,
                        getIntent().getExtras().getString(Constants.ARG_KEY));
                mContactPresent.setContact(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        contact);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        dialogBuilder.show();
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessagingApp.setChatActivityOpen(true);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.ARG_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("online")
                .setValue(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessagingApp.setChatActivityOpen(false);
    }

    @Override
    public void onGetContactSuccess(Contact contact) {
        setTitle(contact.name);
    }

    @Override
    public void onGetContactFailure(String message) {

    }

    @Override
    public void onSetContactSuccess(Contact contact) {
        setTitle(contact.name);
    }

    @Override
    public void onSetContactFailure(String message) {

    }

    @Override
    public void onContactAdded(Contact contact) {

    }

    @Override
    public void onContactChanged(Contact contact) {

    }

    @Override
    public void onContactDeleted(Contact contact) {

    }

    @Override
    public void onCheckUserSuccess(User user) {
        mPartner = user;
        setSubTitle();
    }

    @Override
    public void onCheckUserFailed(String message) {

    }
}
