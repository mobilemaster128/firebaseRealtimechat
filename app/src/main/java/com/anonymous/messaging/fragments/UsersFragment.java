package com.anonymous.messaging.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anonymous.messaging.activity.ChatActivity;
import com.anonymous.messaging.activity.MainActivity;
import com.anonymous.messaging.R;
import com.anonymous.messaging.adapters.ContactListingRecyclerAdapter;
import com.anonymous.messaging.adapters.UserListingRecyclerAdapter;
import com.anonymous.messaging.core.service.ServiceContract;
import com.anonymous.messaging.core.users.GetUsersContract;
import com.anonymous.messaging.core.users.GetUsersPresenter;
import com.anonymous.messaging.events.PushNotificationEvent;
import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.utils.Constants;
import com.anonymous.messaging.utils.ItemClickSupport;
import com.anonymous.messaging.utils.SharedPrefUtil;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment implements GetUsersContract.View, ItemClickSupport.OnItemClickListener, ItemClickSupport.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String ARG_TYPE = "type";
    public static final String TYPE_CHATS = "type_chats";
    public static final String TYPE_ALL = "type_all";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewAllUserListing;
    private TextView mTextBackground;

    private UserListingRecyclerAdapter mUserListingRecyclerAdapter;
    private ContactListingRecyclerAdapter mContactListingRecyclerAdapter;

    private GetUsersPresenter mGetUserPresenter;

    public static UsersFragment newInstance(String type) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_users, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerViewAllUserListing = (RecyclerView) view.findViewById(R.id.recycler_view_all_user_listing);
        mTextBackground = (TextView) view.findViewById(R.id.text_background);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mGetUserPresenter = new GetUsersPresenter(this);
        getUsers();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        ItemClickSupport.addTo(mRecyclerViewAllUserListing)
                .setOnItemClickListener(this);

        ItemClickSupport.addTo(mRecyclerViewAllUserListing)
                .setOnItemLongClickListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        getUsers();
    }

    private void getUsers() {
        if (TextUtils.equals(getArguments().getString(ARG_TYPE), TYPE_CHATS)) {
            mGetUserPresenter.getChatUsers(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } else if (TextUtils.equals(getArguments().getString(ARG_TYPE), TYPE_ALL)) {
            mGetUserPresenter.getAllUsers();
        }
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        if (TextUtils.equals(getArguments().getString(ARG_TYPE), TYPE_CHATS)) {
            ChatActivity.startActivity(getActivity(),
                    mContactListingRecyclerAdapter.getUser(position).name,
                    mContactListingRecyclerAdapter.getUser(position).uId,
                    mContactListingRecyclerAdapter.getUser(position).key);
            getActivity().finish();
        } else if (TextUtils.equals(getArguments().getString(ARG_TYPE), TYPE_ALL)) {
            ChatActivity.startActivity(getActivity(),
                    Constants.NICK_NAME,
                    mUserListingRecyclerAdapter.getUser(position).uId,
                    String.format("%d", System.currentTimeMillis()));
            getActivity().finish();
        }
    }

    @Override
    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
        if (TextUtils.equals(getArguments().getString(ARG_TYPE), TYPE_CHATS)) {
            final Contact contact = mContactListingRecyclerAdapter.getUser(position);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
            dialogBuilder.setTitle(contact.name);
            dialogBuilder.setMessage("Do you want to remove this contact?");
            dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    mGetUserPresenter.deleteChatUsers(FirebaseAuth.getInstance().getCurrentUser().getUid(), contact);
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            Dialog dialog = dialogBuilder.create();
            dialog.show();
        }

        return true;
    }

    @Override
    public void onGetAllUsersSuccess(List<User> users) {
        if (users.size() > 0) {
            mTextBackground.setVisibility(View.INVISIBLE);
        } else {
            mTextBackground.setVisibility(View.VISIBLE);
        }
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mUserListingRecyclerAdapter = new UserListingRecyclerAdapter(users);
        mRecyclerViewAllUserListing.setAdapter(mUserListingRecyclerAdapter);
        mUserListingRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetAllUsersFailure(String message) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        //Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetChatUsersSuccess(List<Contact> contacts) {
        if (contacts.size() > 0) {
            mTextBackground.setVisibility(View.INVISIBLE);
        } else {
            mTextBackground.setVisibility(View.VISIBLE);
        }
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mContactListingRecyclerAdapter = new ContactListingRecyclerAdapter(contacts);
        mRecyclerViewAllUserListing.setAdapter(mContactListingRecyclerAdapter);
        mContactListingRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetChatUsersFailure(String message) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        //Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteChatUsersSuccess(Contact contact) {
        mContactListingRecyclerAdapter.remove(contact);
        if (mContactListingRecyclerAdapter.getItemCount() == 0) {
            mTextBackground.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDeleteChatUsersFailure(String message) {
    }
}
