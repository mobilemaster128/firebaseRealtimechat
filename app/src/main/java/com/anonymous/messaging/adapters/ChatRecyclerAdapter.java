package com.anonymous.messaging.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anonymous.messaging.R;
import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Author: Kartik Sharma
 * Created on: 10/16/2016 , 10:36 AM
 * Project: FirebaseChat
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private List<Chat> mChats;

    public ChatRecyclerAdapter(List<Chat> chats) {
        mChats = chats;
    }

    public void add(Chat chat) {
        mChats.add(chat);
        notifyItemInserted(mChats.size() - 1);
    }

    public void change(Chat chat) {
        for (int index = 0; index < mChats.size(); index++) {
            if (chat.timestamp == mChats.get(index).timestamp) {
                mChats.set(index, chat);
                notifyItemChanged(index);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (TextUtils.equals(mChats.get(position).senderuId,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        Chat chat = mChats.get(position);

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        //String alphabet = Constants.NICK_NAME.substring(0, 1);

        myChatViewHolder.txtChatMessage.setText(chat.message);
        if (chat.read) {
            myChatViewHolder.txtChatMessage.setBackgroundResource(R.drawable.chat_read_bg);
        } else {
            myChatViewHolder.txtChatMessage.setBackgroundResource(R.drawable.chat_sent_bg);
        }
        myChatViewHolder.txtDate.setText(formatter.format(new Date(chat.timestamp)));
        //myChatViewHolder.txtUserAlphabet.setText(alphabet);
    }

    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
        Chat chat = mChats.get(position);

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        //String alphabet = Constants.NICK_NAME.substring(0, 1);

        otherChatViewHolder.txtChatMessage.setText(chat.message);
        if (chat.read) {
            otherChatViewHolder.txtChatMessage.setBackgroundResource(R.drawable.chat_read_bg);
        } else {
            otherChatViewHolder.txtChatMessage.setBackgroundResource(R.drawable.chat_sent_bg);
        }
        otherChatViewHolder.txtDate.setText(formatter.format(new Date(chat.timestamp)));
        //otherChatViewHolder.txtUserAlphabet.setText(alphabet);
    }

    @Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChats.get(position).senderuId,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    public void resetChat() {
        while (mChats.size() > 0) {
            mChats.remove(0);
            notifyItemRemoved(0);
        }
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtDate;//, txtUserAlphabet;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtDate = (TextView) itemView.findViewById(R.id.text_view_date);
            //txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtDate;//, txtUserAlphabet;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtDate = (TextView) itemView.findViewById(R.id.text_view_date);
            //txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }
    }
}
