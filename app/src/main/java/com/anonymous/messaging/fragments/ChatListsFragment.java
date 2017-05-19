package com.anonymous.messaging.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anonymous.messaging.MessagingApp;
import com.anonymous.messaging.R;
import com.anonymous.messaging.activity.ChatActivity;
import com.anonymous.messaging.activity.MainActivity;
import com.anonymous.messaging.activity.WebviewActivity;
import com.anonymous.messaging.adapters.ItemListingRecyclerAdapter;
import com.anonymous.messaging.core.chatlist.ChatListContract;
import com.anonymous.messaging.core.chatlist.ChatListPresenter;
import com.anonymous.messaging.core.contact.ContactContract;
import com.anonymous.messaging.core.contact.ContactPresenter;
import com.anonymous.messaging.core.service.ServiceContract;
import com.anonymous.messaging.core.service.ServicePresenter;
import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.Service;
import com.anonymous.messaging.models.User;
import com.anonymous.messaging.utils.Constants;
import com.anonymous.messaging.utils.ItemClickSupport;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatListsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatListsFragment extends Fragment implements ChatListContract.View, ContactContract.View, ServiceContract.View, ItemClickSupport.OnItemClickListener, ItemClickSupport.OnItemLongClickListener {

    private RecyclerView mRecyclerViewAllUserListing;

    private ItemListingRecyclerAdapter mChatListingRecyclerAdapter;

    private ChatListPresenter mChatListPresenter;
    private ServicePresenter mServicePresenter;
    private ContactPresenter mContactPresenter;
    private TextView mTextBackground;

    public static ChatListsFragment newInstance() {
        ChatListsFragment fragment = new ChatListsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_chatlist, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        mRecyclerViewAllUserListing = (RecyclerView) view.findViewById(R.id.recycler_view_all_user_listing);
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
        mChatListPresenter = new ChatListPresenter(this);
        mContactPresenter = new ContactPresenter(this);
        mChatListPresenter.getChatList(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mContactPresenter.checkContact(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ItemClickSupport.addTo(mRecyclerViewAllUserListing)
                .setOnItemClickListener(this);
        ItemClickSupport.addTo(mRecyclerViewAllUserListing)
                .setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        mChatListingRecyclerAdapter.getItem(position).count(0);
        mChatListingRecyclerAdapter.notifyItemChanged(position);
        Item item = mChatListingRecyclerAdapter.getItem(position);
        if (position == 0) {
            showService(item);
            return;
        }
        if (TextUtils.equals(item.uId, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            ChatActivity.startActivity(getActivity(),
                    item.name,
                    item.senderuId,
                    item.key);
        } else {
            ChatActivity.startActivity(getActivity(),
                    item.name,
                    item.uId,
                    item.key);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mServicePresenter.getService();
    }

    private void showService(Item item) {
        item.count = 0;
        mChatListingRecyclerAdapter.set(0, item);
        MessagingApp.getInstance().setService(item.timestamp);
        WebviewActivity.startActivity(getActivity(), item.name);
/*        MessagingApp.getInstance().setService(item.timestamp);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);

        dialogBuilder.setTitle("Service Message");
        dialogBuilder.setMessage(item.message);
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Item item = mChatListingRecyclerAdapter.getItem(0);
                item.count = 0;
                mChatListingRecyclerAdapter.set(0, item);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        dialogBuilder.show();*/
    }

    @Override
    public boolean onItemLongClicked(RecyclerView recyclerView, final int position, View v) {
        final Item item = mChatListingRecyclerAdapter.getItem(position);
        if (position == 0) {
            showService(item);
            return true;
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        String title = item.name;
        if (title.isEmpty()) {
            title = "Chat with " + Constants.NICK_NAME;
        } else {
            title = "Chat with " + title;
        }
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("Do you want to remove this chat?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mChatListPresenter.deleteFromFirebaseChats(FirebaseAuth.getInstance().getCurrentUser().getUid(), item);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        dialogBuilder.show();
        return true;
    }


    public void setService(Service service) {
        if (mChatListingRecyclerAdapter == null) {
            mChatListingRecyclerAdapter = new ItemListingRecyclerAdapter(new ArrayList<Item>());
            mRecyclerViewAllUserListing.setAdapter(mChatListingRecyclerAdapter);
        }
        String oldService = MessagingApp.getInstance().getService();
        if (TextUtils.equals(oldService, String.valueOf(service.timestamp))) {
            mChatListingRecyclerAdapter.set(0, new Item(service, true));
        } else {
            mChatListingRecyclerAdapter.set(0, new Item(service, false));
        }
    }

    private void updateContact(Contact contact) {
        for (int index = 0; index < mChatListingRecyclerAdapter.count(); index++) {
            if (TextUtils.equals(mChatListingRecyclerAdapter.getItem(index).key, contact.key)) {
                mChatListingRecyclerAdapter.getItem(index).name = contact.name;
                mChatListingRecyclerAdapter.notifyItemChanged(index);
                return;
            }
        }
    }

    public int getItemCount() {
        return mChatListingRecyclerAdapter.count();
    }

    @Override
    public void onGetChatSuccess(Chat chat, String key, String name) {
        if (mChatListingRecyclerAdapter == null) {
            mChatListingRecyclerAdapter = new ItemListingRecyclerAdapter(new ArrayList<Item>());
            mRecyclerViewAllUserListing.setAdapter(mChatListingRecyclerAdapter);
        }
        for (int index = 0; index < mChatListingRecyclerAdapter.count(); index++) {
            if (TextUtils.equals(mChatListingRecyclerAdapter.getItem(index).key, key)) {
                if (TextUtils.equals(mChatListingRecyclerAdapter.getItem(index).senderuId, chat.uId) && !chat.read && TextUtils.equals(FirebaseAuth.getInstance().getCurrentUser().getUid(), chat.uId)) {
                    mChatListingRecyclerAdapter.getItem(index).increase();
                    if (getActivity() != null) ((MainActivity)getActivity()).increaseBadge(1);
                }
                //mChatListingRecyclerAdapter.getItem(index).message = chat.message;
                mChatListingRecyclerAdapter.getItem(index).timestamp = chat.timestamp;
                mChatListingRecyclerAdapter.notifyItemChanged(index);
                return;
            }
        }
        mChatListingRecyclerAdapter.add(new Item(chat, name, key));
        mContactPresenter.getContact(FirebaseAuth.getInstance().getCurrentUser().getUid(), key);
        if (!chat.read && TextUtils.equals(FirebaseAuth.getInstance().getCurrentUser().getUid(), chat.uId)) {
            if (getActivity() != null) ((MainActivity)getActivity()).increaseBadge(1);
        }
        mRecyclerViewAllUserListing.smoothScrollToPosition(mChatListingRecyclerAdapter.getItemCount() - 1);
        if (mChatListingRecyclerAdapter.getItemCount() <= 1) {
            mTextBackground.setVisibility(View.VISIBLE);
        } else {
            mTextBackground.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onChatsDeleted(String key) {
        mChatListingRecyclerAdapter.removeByKey(key);
        if (mChatListingRecyclerAdapter.getItemCount() <= 1) {
            mTextBackground.setVisibility(View.VISIBLE);
        } else {
            mTextBackground.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onGetChatFailure(String message) {
    }

    @Override
    public void onGetContactSuccess(Contact contact) {
        updateContact(contact);
    }

    @Override
    public void onContactAdded(Contact contact) {
        updateContact(contact);
    }

    @Override
    public void onContactChanged(Contact contact) {
        updateContact(contact);
    }

    @Override
    public void onContactDeleted(Contact contact) {

    }

    @Override
    public void onGetContactFailure(String message) {

    }

    @Override
    public void onDeleteChatSuccess(Item item) {
    }

    @Override
    public void onDeleteChatFailure(String message) {
    }

    @Override
    public void onGetServiceSuccess(Service service) {
        setService(service);
    }

    @Override
    public void onGetServiceFailure(String message) {
    }

    @Override
    public void onSetContactSuccess(Contact contact) {

    }

    @Override
    public void onSetContactFailure(String message) {

    }

    @Override
    public void onCheckUserSuccess(User user) {

    }

    @Override
    public void onCheckUserFailed(String message) {

    }
}
