package com.anonymous.messaging.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anonymous.messaging.MessagingApp;
import com.anonymous.messaging.R;
import com.anonymous.messaging.activity.MainActivity;
import com.anonymous.messaging.activity.WebviewActivity;
import com.anonymous.messaging.adapters.ItemListingRecyclerAdapter;
import com.anonymous.messaging.core.say.SayContract;
import com.anonymous.messaging.core.say.SayPresenter;
import com.anonymous.messaging.core.service.ServiceContract;
import com.anonymous.messaging.core.service.ServicePresenter;
import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.Say;
import com.anonymous.messaging.models.Service;
import com.anonymous.messaging.models.Whisper;
import com.anonymous.messaging.utils.Constants;
import com.anonymous.messaging.utils.ItemClickSupport;
import com.anonymous.messaging.utils.SharedPrefUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Use the {@link SayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SayFragment extends Fragment implements SayContract.GetView, ServiceContract.View, ItemClickSupport.OnItemClickListener {
    private ProgressDialog mProgressDialog;

    private RecyclerView mRecyclerViewAllWhisperListing;

    private ItemListingRecyclerAdapter mSayRecyclerAdapter;
    private SayPresenter mSayPresenter;
    private ServicePresenter mServicePresenter;
    private TextView mTextBackground;
    private Button mAddButton;

    // TODO: Rename and change types and number of parameters
    public static SayFragment newInstance() {
        SayFragment fragment = new SayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_say, container, false);

        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        mRecyclerViewAllWhisperListing = (RecyclerView) view.findViewById(R.id.recycler_view_all_say_listing);
        mTextBackground = (TextView) view.findViewById(R.id.text_background);
        mAddButton = (Button) view.findViewById(R.id.add_button);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        mServicePresenter.getService();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        mServicePresenter = new ServicePresenter(this);
        mServicePresenter.getService();
        mSayPresenter = new SayPresenter(this);
        mSayPresenter.getSay(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ItemClickSupport.addTo(mRecyclerViewAllWhisperListing)
                .setOnItemClickListener(this);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity(), R.style.MyAlertDialogTheme);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.say_edit_dialog);
                final EditText text_message = (EditText) dialog.findViewById(R.id.edit_text_message);
                text_message.setMovementMethod(ScrollingMovementMethod.getInstance());
                final Button button_send = (Button) dialog.findViewById(R.id.send_button);
                ImageButton button_cancel = (ImageButton) dialog.findViewById(R.id.cancel_button);
                text_message.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            button_send.setEnabled(true);
                        } else {
                            button_send.setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                button_send.setEnabled(false);
                button_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = text_message.getText().toString();
                        if (!message.isEmpty()) {
                            String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            Say say = new Say(senderUid,
                                    text_message.getText().toString(),
                                    System.currentTimeMillis());
                            ((MainActivity)getActivity()).sendSay(say);
                            dialog.dismiss();
                        }
                    }
                });
                button_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
        });
        DisplayMetrics metrics = new DisplayMetrics(); //get metrics of screen
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int left = metrics.widthPixels / 2 - (int)(metrics.density * 50);
        mAddButton.setPadding(left, 0, 0, 0);
    }

    private void showService(Item item) {
        item.count = 0;
        mSayRecyclerAdapter.set(0, item);
        MessagingApp.getInstance().setService(item.timestamp);
        WebviewActivity.startActivity(getActivity(), item.name);
        /*MessagingApp.getInstance().setService(item.timestamp);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);

        dialogBuilder.setTitle("Service Message");
        dialogBuilder.setMessage(item.message);
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Item item = mSayRecyclerAdapter.getItem(0);
                item.count = 0;
                mSayRecyclerAdapter.set(0, item);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        dialogBuilder.show();*/
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, final int position, View v) {
        mSayRecyclerAdapter.getItem(position).count(0);
        mSayRecyclerAdapter.notifyItemChanged(position);
        final Item item = mSayRecyclerAdapter.getItem(position);
        if (position == 0) {
            showService(item);
            return;
        }
        final Dialog dialog = new Dialog(getActivity(), R.style.MyAlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.say_dialog);
        final TextView text_message = (TextView) dialog.findViewById(R.id.edit_text_message);
        text_message.setMovementMethod(ScrollingMovementMethod.getInstance());
        Button button_resend = (Button) dialog.findViewById(R.id.resend_button);
        Button button_delete = (Button) dialog.findViewById(R.id.delete_button);
        ImageButton button_cancel = (ImageButton) dialog.findViewById(R.id.cancel_button);
        text_message.setText(item.message);
        button_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).resendSay(new Say(item));
                dialog.dismiss();
            }
        });
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).deleteSay(new Say(item));
                dialog.dismiss();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void setService(Service service) {
        if (mSayRecyclerAdapter == null) {
            mSayRecyclerAdapter = new ItemListingRecyclerAdapter(new ArrayList<Item>());
            mRecyclerViewAllWhisperListing.setAdapter(mSayRecyclerAdapter);
        }
        String oldService = MessagingApp.getInstance().getService();
        if (TextUtils.equals(oldService, String.valueOf(service.timestamp))) {
            mSayRecyclerAdapter.set(0, new Item(service, true));
        } else {
            mSayRecyclerAdapter.set(0, new Item(service, false));
        }
    }

    @Override
    public void onAddSay(Say say) {
        if (mSayRecyclerAdapter == null) {
            mSayRecyclerAdapter = new ItemListingRecyclerAdapter(new ArrayList<Item>());
            mRecyclerViewAllWhisperListing.setAdapter(mSayRecyclerAdapter);
        }
        mSayRecyclerAdapter.add(new Item(say, Constants.SAY_TYPE));
        mRecyclerViewAllWhisperListing.smoothScrollToPosition(mSayRecyclerAdapter.getItemCount() - 1);
        if (mSayRecyclerAdapter.getItemCount() <= 1) {
            mTextBackground.setVisibility(View.VISIBLE);
        } else {
            mTextBackground.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSayFailure(String message) {
        //Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteSay(Say say) {
        mSayRecyclerAdapter.removeBySay(say);
        if (mSayRecyclerAdapter.getItemCount() <= 1) {
            mTextBackground.setVisibility(View.VISIBLE);
        } else {
            mTextBackground.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onGetServiceSuccess(Service service) {
        setService(service);
    }

    @Override
    public void onGetServiceFailure(String message) {
    }
}
