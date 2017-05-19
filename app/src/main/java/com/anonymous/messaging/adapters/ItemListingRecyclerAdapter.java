package com.anonymous.messaging.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anonymous.messaging.R;
import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.models.Item;
import com.anonymous.messaging.models.Say;
import com.anonymous.messaging.models.Service;
import com.anonymous.messaging.models.Whisper;
import com.anonymous.messaging.utils.Constants;
import com.anonymous.messaging.utils.SharedPrefUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 2:23 PM
 * Project: FirebaseChat
 */

public class ItemListingRecyclerAdapter extends RecyclerView.Adapter<ItemListingRecyclerAdapter.ViewHolder> {
    private List<Item> mItems;

    public ItemListingRecyclerAdapter(List<Item> items) {
        Item service = new Item(new Service("No Message", "", System.currentTimeMillis()));
        if (mItems == null) {
            mItems = new ArrayList<Item>();
        }
        mItems.add(service);
        for (int index = 0; index < items.size(); index++) {
            this.mItems.add(items.get(index));
        }
    }

    public void set(int index, Item item) {
        if (mItems == null) {
            mItems = new ArrayList<Item>();
        }
        if (index == 0 || index < mItems.size()) {
            mItems.set(index, item);
            notifyItemChanged(index);
        }
    }

    public void add(Item item) {
        mItems.add(item);
        notifyItemInserted(mItems.size() - 1);
    }

    public void reset() {
        while (mItems.size() > 0) {
            mItems.remove(0);
            notifyItemRemoved(0);
        }
    }

    public int count() {
        return mItems.size();
    }

    public void remove(Item item) {
        int position = mItems.indexOf(item);
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public void removeBySay(Say say) {
        for (int index = 0; index < mItems.size(); index++) {
            if (TextUtils.equals(mItems.get(index).senderuId, say.uId) && mItems.get(index).timestamp == say.timestamp) {
                mItems.remove(index);
                notifyItemRemoved(index);
            }
        }
    }

    public void removeByWhisper(Whisper whipser) {
        for (int index = 0; index < mItems.size(); index++) {
            if (TextUtils.equals(mItems.get(index).senderuId, whipser.uId) && mItems.get(index).timestamp == whipser.timestamp) {
                mItems.remove(index);
                notifyItemRemoved(index);
            }
        }
    }

    public void removeByChat(Chat chat) {
        for (int index = 0; index < mItems.size(); index++) {
        }
    }

    public void removeByKey(String key) {
        for (int index = 0; index < mItems.size(); index++) {
            if (TextUtils.equals(mItems.get(index).key, key)) {
                mItems.remove(index);
                notifyItemRemoved(index);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mItems.get(position);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        switch (item.item_type) {
            case Constants.SAY_TYPE:
                holder.txtItemTitle.setText("My Whisper");
                if (item.message.length() > 25) {
                    holder.txtItemMessage.setText(item.message.substring(0, 25) + "...");
                } else {
                    holder.txtItemMessage.setText(item.message);
                }
                if (item.count > 0) {
                    holder.txtItemCount.setText(String.format("%d", item.count));
                    holder.txtItemCount.setVisibility(View.VISIBLE);
                } else {
                    holder.txtItemCount.setVisibility(View.INVISIBLE);
                }
                holder.txtItemDate.setText(formatter.format(new Date(item.timestamp)));
                break;
            case Constants.WHISPER_TYPE:
                holder.txtItemTitle.setText("Whisper from " + item.name);
                if (item.message.length() > 25) {
                    holder.txtItemMessage.setText(item.message.substring(0, 25) + "...");
                } else {
                    holder.txtItemMessage.setText(item.message);
                }
                if (item.count > 0) {
                    holder.txtItemCount.setText(String.format("%d", item.count));
                    holder.txtItemCount.setVisibility(View.VISIBLE);
                } else {
                    holder.txtItemCount.setVisibility(View.INVISIBLE);
                }
                holder.txtItemDate.setText(formatter.format(new Date(item.timestamp)));
                break;
            case Constants.SERVICE_TYPE:
                holder.txtItemTitle.setText("yril community");
                if (item.message.length() > 25) {
                    holder.txtItemMessage.setText(item.message.substring(0, 25) + "...");
                } else {
                    holder.txtItemMessage.setText(item.message);
                }
                holder.txtItemCount.setVisibility(View.INVISIBLE);
                if (item.count > 0) {
                    holder.itemView.setBackgroundResource(R.color.pink_200);
                } else {
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                }
                holder.txtItemDate.setText(formatter.format(new Date(item.timestamp)));
                break;
            case Constants.CHAT_TYPE:
                if (item.name.isEmpty()) {
                    holder.txtItemTitle.setText(Constants.NICK_NAME);
                } else {
                    holder.txtItemTitle.setText(item.name);
                }
                if (item.message.length() > 25) {
                    holder.txtItemMessage.setText(item.message.substring(0, 25) + "...");
                } else {
                    holder.txtItemMessage.setText(item.message);
                }
                if (item.count > 0) {
                    holder.txtItemCount.setText(String.format("%d", item.count));
                    holder.txtItemCount.setVisibility(View.VISIBLE);
                } else {
                    holder.txtItemCount.setVisibility(View.INVISIBLE);
                }
                holder.txtItemDate.setText(formatter.format(new Date(item.timestamp)));
                break;
        }

    }

    @Override
    public int getItemCount() {
        if (mItems != null) {
            return mItems.size();
        }
        return 0;
    }

    public Item getItem(int position) {
        return mItems.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtItemTitle, txtItemMessage, txtItemCount, txtItemDate;

        ViewHolder(View itemView) {
            super(itemView);
            txtItemTitle = (TextView) itemView.findViewById(R.id.text_view_title);
            txtItemMessage = (TextView) itemView.findViewById(R.id.text_view_message);
            txtItemCount = (TextView) itemView.findViewById(R.id.text_view_count);
            txtItemDate = (TextView) itemView.findViewById(R.id.text_view_date);
        }
    }
}
