package com.anonymous.messaging.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anonymous.messaging.MessagingApp;
import com.anonymous.messaging.R;
import com.anonymous.messaging.activity.MainActivity;
import com.anonymous.messaging.activity.WebviewActivity;
import com.anonymous.messaging.adapters.ItemListingRecyclerAdapter;
import com.anonymous.messaging.core.hear.HearContract;
import com.anonymous.messaging.core.hear.HearPresenter;
import com.anonymous.messaging.core.service.ServiceContract;
import com.anonymous.messaging.core.service.ServicePresenter;
import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.SayUser;
import com.anonymous.messaging.models.Service;
import com.anonymous.messaging.models.Whisper;
import com.anonymous.messaging.utils.Constants;
import com.anonymous.messaging.utils.ItemClickSupport;
import com.anonymous.messaging.utils.SharedPrefUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HearFragment extends Fragment implements HearContract.GetView, ServiceContract.View, ItemClickSupport.OnItemClickListener {
    private RecyclerView mRecyclerViewAllWhisperListing;

    private ItemListingRecyclerAdapter mWhisperListingRecyclerAdapter;

    private HearPresenter mHearPresenter;
    private ServicePresenter mServicePresenter;
    private TextView mTextBackground;

    public static HearFragment newInstance() {
        HearFragment fragment = new HearFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_hear, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        mRecyclerViewAllWhisperListing = (RecyclerView) view.findViewById(R.id.recycler_view_all_whisper_listing);
        mTextBackground = (TextView) view.findViewById(R.id.text_background);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mServicePresenter = new ServicePresenter(this);
        mServicePresenter.getService();
        mHearPresenter = new HearPresenter(this);
        mHearPresenter.getHear(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ItemClickSupport.addTo(mRecyclerViewAllWhisperListing)
                .setOnItemClickListener(this);
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, final int position, View v) {
        mWhisperListingRecyclerAdapter.getItem(position).count(0);
        mWhisperListingRecyclerAdapter.notifyItemChanged(position);
        final Item item = mWhisperListingRecyclerAdapter.getItem(position);
        if (position == 0) {
            showService(item);
            return;
        }
        final Dialog dialog = new Dialog(getActivity(), R.style.MyAlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.hear_dialog);
        final Toolbar toolbar = (Toolbar) dialog.findViewById(R.id.toolbar);
        toolbar.setTitle("Add Nickname");

        SayUser sayUser = ((MainActivity)getActivity()).getSayUser(item.senderuId);
        if (sayUser == null) {
            toolbar.setSubtitle("Error");
        } else {
            long today = (getStartOfDay(new Date()).getTime() - sayUser.timeStamp)/(24*60*60*1000);
            if (today < 1) {
                if (sayUser.online) {
                    toolbar.setSubtitle("Online");
                } else {
                    //SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
                    toolbar.setSubtitle("Today");//(formatter.format(last));
                }
            } else if (today < 2) {
                toolbar.setSubtitle("Yesterday");
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
                toolbar.setSubtitle(formatter.format(sayUser.timeStamp));
            }
        }
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
                dialogBuilder.setTitle("Input Name");// Set up the input
                final EditText nameEdit = new EditText(getActivity());
                nameEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                nameEdit.setHint("name");
                nameEdit.setText("");
                nameEdit.setTextColor(Color.WHITE);
                nameEdit.setHintTextColor(Color.GRAY);
                LinearLayout view = new LinearLayout(getActivity());
                view.setOrientation(LinearLayout.VERTICAL);
                view.setPadding(24, 24, 24, 24);
                view.addView(nameEdit);
                dialogBuilder.setView(view);
                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String name = nameEdit.getText().toString();
                        if (!name.isEmpty()) {
                            toolbar.setTitle(name);
                        }
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                dialogBuilder.show();
            }
        });
        final TextView text_message = (TextView) dialog.findViewById(R.id.text_view_message);
        text_message.setMovementMethod(ScrollingMovementMethod.getInstance());
        Button button_reply = (Button) dialog.findViewById(R.id.reply_button);
        Button button_pass = (Button) dialog.findViewById(R.id.delete_button);
        Button button_report = (Button) dialog.findViewById(R.id.report_button);
        text_message.setText(item.message);
        button_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = toolbar.getTitle().toString();
                if (TextUtils.equals(name, "Add Nickname")) {
                    name = "";
                }
                ((MainActivity)getActivity()).replyHear(new Whisper(item), name);
                dialog.dismiss();
            }
        });
        button_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).passHear(new Whisper(item));
                dialog.dismiss();
            }
        });
        button_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).reportHear(new Whisper(item));
                dialog.dismiss();
            }
        });
        dialog.show(); //first show
        DisplayMetrics metrics = new DisplayMetrics(); //get metrics of screen
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = (int) (metrics.heightPixels*0.95); //set height to 90% of total
        int width = (int) (metrics.widthPixels*0.95); //set width to 90% of total
        dialog.getWindow().setLayout(width, height); //set layout*/
    }

    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
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
    public void onResume() {
        super.onResume();
        mServicePresenter.getService();
    }

    private void showService(Item item) {
        item.count = 0;
        mWhisperListingRecyclerAdapter.set(0, item);
        MessagingApp.getInstance().setService(item.timestamp);
        WebviewActivity.startActivity(getActivity(), item.name);
        /*MessagingApp.getInstance().setService(item.timestamp);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);

        dialogBuilder.setTitle("Service Message");
        dialogBuilder.setMessage(item.message);
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Item item = mWhisperListingRecyclerAdapter.getItem(0);
                item.count = 0;
                mWhisperListingRecyclerAdapter.set(0, item);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        dialogBuilder.show();*/
    }

    public void setService(Service service) {
        if (mWhisperListingRecyclerAdapter == null) {
            mWhisperListingRecyclerAdapter = new ItemListingRecyclerAdapter(new ArrayList<Item>());
            mRecyclerViewAllWhisperListing.setAdapter(mWhisperListingRecyclerAdapter);
        }
        String oldService = MessagingApp.getInstance().getService();
        if (TextUtils.equals(oldService, String.valueOf(service.timestamp))) {
            mWhisperListingRecyclerAdapter.set(0, new Item(service, true));
        } else {
            mWhisperListingRecyclerAdapter.set(0, new Item(service, false));
        }
    }

    public int getItemCount() {
        return mWhisperListingRecyclerAdapter.count();
    }

    @Override
    public void onAddHear(Whisper whisper) {
        if (mWhisperListingRecyclerAdapter == null) {
            mWhisperListingRecyclerAdapter = new ItemListingRecyclerAdapter(new ArrayList<Item>());
            mRecyclerViewAllWhisperListing.setAdapter(mWhisperListingRecyclerAdapter);
        }
        for (int index = 0; index < mWhisperListingRecyclerAdapter.count(); index++) {
            if (mWhisperListingRecyclerAdapter.getItem(index).timestamp == whisper.timestamp) {
                mWhisperListingRecyclerAdapter.getItem(index).increase();
                mWhisperListingRecyclerAdapter.getItem(index).message = whisper.message;
                mWhisperListingRecyclerAdapter.getItem(index).timestamp = whisper.timestamp;
                mWhisperListingRecyclerAdapter.notifyItemChanged(index);
                return;
            }
        }
        mWhisperListingRecyclerAdapter.add(new Item(whisper, Constants.WHISPER_TYPE));
        mRecyclerViewAllWhisperListing.smoothScrollToPosition(mWhisperListingRecyclerAdapter.getItemCount() - 1);
        if (mWhisperListingRecyclerAdapter.getItemCount() <= 1) {
            mTextBackground.setVisibility(View.VISIBLE);
        } else {
            mTextBackground.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDeleteHear(Whisper whisper) {
        mWhisperListingRecyclerAdapter.removeByWhisper(whisper);
        if (mWhisperListingRecyclerAdapter.getItemCount() <= 1) {
            mTextBackground.setVisibility(View.VISIBLE);
        } else {
            mTextBackground.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onHearFailure(String message) {
    }

    @Override
    public void onGetServiceSuccess(Service service) {
        setService(service);
    }

    @Override
    public void onGetServiceFailure(String message) {
    }
}
