package com.anonymous.messaging.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.anonymous.messaging.MessagingApp;
import com.anonymous.messaging.R;
import com.anonymous.messaging.components.BadgeTabLayout;
import com.anonymous.messaging.core.event.EventContract;
import com.anonymous.messaging.core.event.EventPresenter;
import com.anonymous.messaging.core.hear.HearContract;
import com.anonymous.messaging.core.hear.HearPresenter;
import com.anonymous.messaging.core.say.SayContract;
import com.anonymous.messaging.core.say.SayPresenter;
import com.anonymous.messaging.core.user.UserContract;
import com.anonymous.messaging.core.user.UserPresenter;
import com.anonymous.messaging.events.PushNotificationEvent;
import com.anonymous.messaging.fragments.ChatListsFragment;
import com.anonymous.messaging.fragments.HearFragment;
import com.anonymous.messaging.fragments.SayFragment;
import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Say;
import com.anonymous.messaging.models.SayUser;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.models.Whisper;
import com.anonymous.messaging.utils.Constants;
import com.anonymous.messaging.utils.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements UserContract.View, EventContract.View, SayContract.SetView, HearContract.SetView {

    private Toolbar mToolbar;
    private Dialog dialog;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private BadgeTabLayout mTabLayout;
    //private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private UserPresenter mUserPresenter;
    private EventPresenter mEventPresenter;
    private SayPresenter mSayPresenter;
    private HearPresenter mHearPresenter;
    private User mUser;
    private List<SayUser> mSayUsers;
    private boolean sayContinue;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int flags) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(flags);
        context.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        MessagingApp.setMainActivityOpen(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        MessagingApp.setMainActivityOpen(true);
        if (getIntent().hasExtra(Constants.ARG_TITLE)) {
            EventBus.getDefault().post(new PushNotificationEvent(getIntent().getExtras().getString(Constants.ARG_TITLE),
                    getIntent().getExtras().getString(Constants.ARG_MESSAGE),
                    getIntent().getExtras().getString(Constants.ARG_KEY),
                    Constants.NICK_NAME,
                    getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID)));
            getIntent().putExtra(Constants.ARG_TITLE, "");
        }
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.ARG_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("online")
                .setValue(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout = (BadgeTabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.with(0).setTitle(mSectionsPagerAdapter.getPageTitle(0).toString()).badgeCount(0).icon(R.drawable.ic_action_hear).build();
        mTabLayout.with(1).setTitle(mSectionsPagerAdapter.getPageTitle(1).toString()).badgeCount(0).icon(R.drawable.ic_action_chat).build();
        mTabLayout.with(2).setTitle(mSectionsPagerAdapter.getPageTitle(2).toString()).badgeCount(0).icon(R.drawable.ic_action_say).build();

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_home);

        getSupportActionBar().setTitle("You Rant I Listen");

        mUserPresenter = new UserPresenter(this);
        mEventPresenter = new EventPresenter(this);
        mSayPresenter = new SayPresenter(this);
        mHearPresenter = new HearPresenter(this);

        mUser = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null),
                "",
                0,
                true);

        // Attach the page change listener inside the activity
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                mTabLayout.with(position).badgeCount(0).build();
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });
        mUserPresenter.getUser(mUser.uId);
        mEventPresenter.checkHears(mUser.uId);
        mEventPresenter.checkContacts(mUser.uId);
        mEventPresenter.checkUsers(mUser.uId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
            case R.id.action_lock:
                final String curPass = new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_PASSWORD);
                if (curPass == null || curPass.isEmpty()) {
                    openLockDialog();
                } else {
                    dialog = new Dialog(this);//, R.style.LockDialogTheme);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_dialog);
                    final TextView textTitle = (TextView) dialog.findViewById(R.id.text_title);
                    final TextView textMessage = (TextView) dialog.findViewById(R.id.text_message);
                    Button button_cancel = (Button) dialog.findViewById(R.id.cancel_button);
                    Button button_ok = (Button) dialog.findViewById(R.id.ok_button);
                    textTitle.setText("Lock App");
                    textMessage.setText("Do you want to lock App?");
                    button_cancel.setText("Change Code");
                    button_ok.setText("Lock");
                    button_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mUserPresenter.lockUser(mUser.uId, curPass);
                            dialog.dismiss();
                        }
                    });
                    button_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            openLockDialog();
                        }
                    });
                    //dialog.setCancelable(false);
                    dialog.show(); //first show
                    DisplayMetrics metrics = new DisplayMetrics(); //get metrics of screen
                    this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int width = (int) (metrics.widthPixels); //set width to 90% of total
                    dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT); //set layout*/

                    /*AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                    dialogBuilder.setTitle("Lock App");
                    dialogBuilder.setMessage("Do you want to lock App?");
                    dialogBuilder.setPositiveButton("Lock", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mUserPresenter.lockUser(mUser.uId, curPass);
                        }
                    });
                    dialogBuilder.setNegativeButton("Change Code", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            openLockDialog();
                        }
                    });
                    dialogBuilder.show();*/
                }
                break;
            case R.id.action_direct_message:
                DirectMessageActivity.startIntent(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openLockDialog() {
        dialog = new Dialog(this);//, R.style.LockDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.lock_dialog);
        final EditText passwordEdit = (EditText) dialog.findViewById(R.id.edit_text_password);
        final EditText confirmEdit = (EditText) dialog.findViewById(R.id.edit_text_confirm);
        ImageButton button_cancel = (ImageButton) dialog.findViewById(R.id.cancel_button);
        Button button_lock = (Button) dialog.findViewById(R.id.lock_button);
        button_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = passwordEdit.getText().toString();
                String pass1 = confirmEdit.getText().toString();
                if (pass.isEmpty()) {
                    passwordEdit.setError("empty code");
                    confirmEdit.setError("empty code");
                } else if (TextUtils.equals(pass, pass1)) {
                    new SharedPrefUtil(getApplicationContext()).saveString(Constants.ARG_PASSWORD, pass);
                    mUserPresenter.lockUser(mUser.uId, pass);
                    dialog.dismiss();
                } else {
                    confirmEdit.setError("doesn't match");
                    passwordEdit.setText("");
                    confirmEdit.setText("");
                }
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show(); //first show
        DisplayMetrics metrics = new DisplayMetrics(); //get metrics of screen
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = (int) (metrics.widthPixels * 0.9); //set width to 90% of total
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT); //set layout*/
    }

    public void lockApp() {
        dialog = new Dialog(this, R.style.LockDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.locked_dialog);
        final EditText passwordEdit = (EditText) dialog.findViewById(R.id.edit_password);
        Button button_unlock = (Button) dialog.findViewById(R.id.unlock_button);
        Button button_forgot = (Button) dialog.findViewById(R.id.forgot_button);
        button_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = passwordEdit.getText().toString();
                if (pass.isEmpty()) {
                    passwordEdit.setError("empty code");
                } else {
                    mUserPresenter.unlockUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), pass);
                }
            }
        });
        button_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() > mUser.timeStamp){
                    mUserPresenter.forgot(mUser.uId);
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show(); //first show
        if (System.currentTimeMillis() < mUser.timeStamp){
            //button_unlock.setEnabled(false);
            //passwordEdit.setEnabled(false);
            forgotTimer();
        }
    }

    @Override
    protected void onDestroy() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.ARG_USERS)
                .child(mUser.uId)
                .child("online")
                .setValue(false);
        super.onDestroy();
    }

    private void forgotTimer() {
        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                long timediff = mUser.timeStamp - System.currentTimeMillis();
                if (timediff > 0) {
                    long min = timediff / 60000;
                    long sec = timediff/1000 - min*60;
                    ((Button)dialog.findViewById(R.id.forgot_button)).setText(String.format("App will be unlocked in %d:%2d", min, sec));
                    handler.postDelayed(this, 1000);
                } else {
                    ((Button)dialog.findViewById(R.id.forgot_button)).setText("Forgot Code");
                    mUserPresenter.unlockUser(mUser.uId, mUser.password);
                    //dialog.findViewById(R.id.unlock_button).setEnabled(true);
                    //dialog.findViewById(R.id.edit_password).setEnabled(true);
                }
            }
        };
        handler.postDelayed(r, 1000);
    }

    public void reportHear(Whisper whisper) {
        mHearPresenter.reportHear(mUser.uId, whisper);
    }

    public void replyHear(final Whisper whisper, final String name) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        dialogBuilder.setTitle("Input your message");
        final EditText messageEdit = new EditText(this);
        messageEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        messageEdit.setHint("type your message");
        messageEdit.setLines(5);
        messageEdit.setGravity(Gravity.TOP);
        messageEdit.setTextColor(Color.WHITE);
        messageEdit.setHintTextColor(Color.GRAY);
        LinearLayout view = new LinearLayout(this);
        view.setPadding(24, 24, 24, 24);
        view.setOrientation(LinearLayout.VERTICAL);
        view.addView(messageEdit);
        dialogBuilder.setView(view);
        dialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Chat chat = new Chat(whisper.uId, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        messageEdit.getText().toString(), System.currentTimeMillis(), false);
                mHearPresenter.replyToHear(mUser.uId, whisper, chat, name);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        dialogBuilder.show();
    }

    public void passHear(Whisper whisper) {
        mHearPresenter.passHear(mUser.uId, whisper);
    }

    public void sendSay(Say say) {
        mSayPresenter.addSay(say);
        sayContinue = true;
        resetSayUsers();
    }

    public void resendSay(Say say) {
        resetSayUsers();
        sayContinue = true;
        circulateSay(say);
    }

    public void deleteSay(Say say) {
        sayContinue = false;
        mSayPresenter.deleteSay(String.valueOf(say.timestamp));
    }

    public void resetSayUsers() {
        for (int position = 0; position < mSayUsers.size(); position++) {
            mSayUsers.get(position).sent = false;
        }
    }

    public void circulateSay(final Say say) {
        final Handler handler = new Handler();
        final Random random = new Random();

        final Runnable r = new Runnable() {
            public void run() {
                if (mSayUsers.size() == 0) return;
                int index = random.nextInt(mSayUsers.size());
                int position = index;
                while (mSayUsers.get(index).sent) {
                    index++;
                    if (index >= mSayUsers.size()) index = 0;
                    if (index == position) {
                        return;
                    }
                }
                if (sayContinue) {
                    if (mSayUsers.get(index).uId != null){
                        Log.d("say", mSayUsers.get(index).uId);
                        mSayPresenter.sendSay(say, mSayUsers.get(index).uId);
                    } else {
                        mSayUsers.get(index).sent = true;
                    }
                    handler.postDelayed(this, Constants.SAY_CYCLE);
                }
            }
        };
        handler.post(r);
    }

    public SayUser getSayUser(String uId) {
        for (int position = 0; position < mSayUsers.size(); position++) {
            if (TextUtils.equals(mSayUsers.get(position).uId, uId)) {
                return mSayUsers.get(position);
            }
        }
        return null;
    }

    public void changeBadge(final int index, final int count) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTabLayout.with(index).badgeCount(count).build();
            }
        });
    }

    public void increaseBadge(final int index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTabLayout.with(index).increase().build();
            }
        });
    }

    public void decreaseBadge(final int index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTabLayout.with(index).decrease().build();
            }
        });
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return HearFragment.newInstance();
            } else if (position == 1) {
                //return UsersFragment.newInstance(UsersFragment.TYPE_ALL);
                return ChatListsFragment.newInstance();
            } else {
                return SayFragment.newInstance();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return  fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Hear";
                case 1:
                    return "Chats";
                case 2:
                    return "Say";
            }
            return null;
        }

        public Fragment getFragmentByPosition(int position) {
            return registeredFragments.get(position);
        }
    }

    @Override
    public void onGetUserSuccess(User user) {
        this.mUser = user;
        if (!user.password.isEmpty()) {
            lockApp();
        }
    }

    @Override
    public void onGetUserFailure(String message) {

    }

    @Override
    public void onLockUserSuccess(String password) {
        //LockActivity.startIntent(this, 0);
        this.mUser.password = password;
        lockApp();
    }

    @Override
    public void onLockUserFailure(String message) {

    }

    @Override
    public void onUnlockUserSuccess(String message) {
        this.mUser.password = "";
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onUnlockUserFailure(String message) {
        ((EditText)dialog.findViewById(R.id.edit_password)).setError("wrong code");
    }

    @Override
    public void onForgotSuccess(long timeStamp) {
        mUser.timeStamp = timeStamp;
        forgotTimer();
        //dialog.findViewById(R.id.unlock_button).setEnabled(false);
        //dialog.findViewById(R.id.edit_password).setEnabled(false);
    }

    @Override
    public void onForgotFailure(String message) {

    }

    public void setTabTitle(final int index, final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTabLayout.with(index).setTitle(mSectionsPagerAdapter.getPageTitle(index) + title).build();
            }
        });
    }

    @Override
    public void onCheckContacts(long count) {
        if (count >= 10) {
            setTabTitle(1, "(10)");
        } else {
            setTabTitle(1, "");
        }
    }

    @Override
    public void onCheckHears(long count) {
        if (count >= 10) {
            setTabTitle(0, "(10)");
        } else {
            setTabTitle(0, "");
        }
    }

    @Override
    public void onAddUser(User user) {
        if (mSayUsers == null) {
            mSayUsers = new ArrayList<>();
        }
        if (!TextUtils.equals(user.uId, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            mSayUsers.add(new SayUser(user, false));
        }
    }

    @Override
    public void onChangeUser(User user) {
        int position;
        for (position = 0; position < mSayUsers.size(); position++) {
            if (TextUtils.equals(mSayUsers.get(position).uId, user.uId)){
                mSayUsers.get(position).firebaseToken = user.firebaseToken;
                mSayUsers.get(position).timeStamp = user.timeStamp;
                break;
            }
        }
    }

    @Override
    public void onRemoveUser(User user) {
        for (int position = 0; position < mSayUsers.size(); position++) {
            if (TextUtils.equals(mSayUsers.get(position).uId, user.uId)){
                mSayUsers.remove(position);
                break;
            }
        }
    }

    @Override
    public void onAddSaySuccess(Say say) {
        circulateSay(say);
    }

    @Override
    public void onAddSayFailure(String message) {

    }

    @Override
    public void onSendSaySuccess(Say say, String uId) {
        for (int position = 0; position < mSayUsers.size(); position++) {
            if (TextUtils.equals(mSayUsers.get(position).uId, uId)){
                mSayUsers.get(position).sent = true;
                break;
            }
        }
    }

    @Override
    public void onSendSayFailure(String message, String uId) {

    }

    @Override
    public void onDeleteSaySuccess(String key) {

    }

    @Override
    public void onDeleteSayFailure(String message) {

    }

    @Override
    public void onReportSaySuccess(Say say) {

    }

    @Override
    public void onReportSayFailure(String message) {

    }

    @Override
    public void onAddHearSuccess(Whisper whisper) {
        increaseBadge(0);
    }

    @Override
    public void onAddHearFailure(String message) {
    }

    @Override
    public void onReplyToHearSuccess(Whisper whisper) {
    }

    @Override
    public void onReplyToHearFailure(String message) {
    }

    @Override
    public void onDeleteHearSuccess(Whisper whisper) {
    }

    @Override
    public void onDeleteHearFailure(String message) {
    }

    @Override
    public void onReportHearSuccess(Whisper whisper) {
    }

    @Override
    public void onReportHearFailure(String message) {
    }

    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        if (TextUtils.equals(pushNotificationEvent.getTitle(), Constants.WHISPER_NOTI)) {
            Whisper whisper = new Whisper(pushNotificationEvent.getUid(),
                    pushNotificationEvent.getMessage(),
                    Long.parseLong(pushNotificationEvent.getKey()));
            mHearPresenter.addHear(FirebaseAuth.getInstance().getCurrentUser().getUid(), whisper);
            //((HearFragment) mSectionsPagerAdapter.getFragmentByPosition(0)).addHear(whisper);
        } else if (TextUtils.equals(pushNotificationEvent.getTitle(), Constants.REPORT_NOTI)) {
            sayContinue = false;
            Say say = new Say(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    pushNotificationEvent.getMessage(),
                    Long.parseLong(pushNotificationEvent.getKey()));
            mSayPresenter.reportSay(say, pushNotificationEvent.getUid());
        } else if (TextUtils.equals(pushNotificationEvent.getTitle(), Constants.REPLY_NOTI)) {
            sayContinue = false;
            mSayPresenter.deleteSay(pushNotificationEvent.getKey());
        } else {
            //increaseBadge(1);
        }
    }
}
