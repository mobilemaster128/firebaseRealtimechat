package com.anonymous.messaging.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anonymous.messaging.R;
import com.anonymous.messaging.models.Contact;
import com.anonymous.messaging.models.Item;

import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 2:23 PM
 * Project: FirebaseChat
 */

public class ContactListingRecyclerAdapter extends RecyclerView.Adapter<ContactListingRecyclerAdapter.ViewHolder> {
    private List<Contact> mContacts;

    public ContactListingRecyclerAdapter(List<Contact> contact) {
        this.mContacts = contact;
    }

    public void add(Contact contact) {
        mContacts.add(contact);
        notifyItemInserted(mContacts.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_user_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = mContacts.get(position);

        String alphabet = contact.name.substring(0, 1);

        holder.txtUsername.setText(contact.name);
        holder.txtUserAlphabet.setText(alphabet);
    }

    @Override
    public int getItemCount() {
        if (mContacts != null) {
            return mContacts.size();
        }
        return 0;
    }

    public void remove(Contact contact) {
        int position = mContacts.indexOf(contact);
        mContacts.remove(position);
        notifyItemRemoved(position);
    }

    public Contact getUser(int position) {
        return mContacts.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUserAlphabet, txtUsername;

        ViewHolder(View itemView) {
            super(itemView);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
            txtUsername = (TextView) itemView.findViewById(R.id.text_view_username);
        }
    }
}
