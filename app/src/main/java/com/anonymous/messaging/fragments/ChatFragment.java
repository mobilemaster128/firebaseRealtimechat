package com.anonymous.messaging.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;

import com.anonymous.messaging.BuildConfig;
import com.anonymous.messaging.activity.ChatActivity;
import com.anonymous.messaging.R;
import com.anonymous.messaging.adapters.ChatRecyclerAdapter;
import com.anonymous.messaging.core.block.BlockContract;
import com.anonymous.messaging.core.block.BlockPresenter;
import com.anonymous.messaging.core.chat.ChatContract;
import com.anonymous.messaging.core.chat.ChatPresenter;
import com.anonymous.messaging.events.PushNotificationEvent;
import com.anonymous.messaging.models.Chat;
import com.anonymous.messaging.utils.Constants;
import com.anonymous.messaging.utils.ItemClickSupport;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import static android.app.Activity.RESULT_OK;


/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 10:36 AM
 * Project: FirebaseChat
 */

public class ChatFragment extends Fragment implements ChatContract.View, BlockContract.View, ItemClickSupport.OnItemClickListener {
    private RecyclerView mRecyclerViewChat;
    private EditText mTextMessage;
    private ImageButton mSendButton;
    private ImageButton mPhotoButton;
    private ProgressDialog mProgressDialog;
    private boolean mHisBlocked = false;
    private boolean mMyBlocked = false;

    private ChatRecyclerAdapter mChatRecyclerAdapter;

    private ChatPresenter mChatPresenter;
    private BlockPresenter mBlockPresenter;
    private long mChatCount;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private File filePathImageCamera;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;

    public static ChatFragment newInstance(String name,
                                           String receiverUid,
                                           String key) {
        Bundle args = new Bundle();
        args.putString(Constants.ARG_NAME, name);
        args.putString(Constants.ARG_RECEIVER_UID, receiverUid);
        args.putString(Constants.ARG_KEY, key);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_chat, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }
    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    public void block() {
        mBlockPresenter.blockUser(FirebaseAuth.getInstance().getCurrentUser().getUid() + '_' +  getArguments().getString(Constants.ARG_KEY));
    }

    public void unBlock() {
        mBlockPresenter.unBlockUser(FirebaseAuth.getInstance().getCurrentUser().getUid() + '_' +  getArguments().getString(Constants.ARG_KEY));
    }

    private void bindViews(View view) {
        mRecyclerViewChat = (RecyclerView) view.findViewById(R.id.recycler_view_chat);
        mTextMessage = (EditText) view.findViewById(R.id.edit_text_message);
        mSendButton = (ImageButton) view.findViewById(R.id.send_button);
        mPhotoButton = (ImageButton) view.findViewById(R.id.image_button);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        mSendButton.setEnabled(false);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verifyStoragePermissions();
            }
        });

        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.checkMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                getArguments().getString(Constants.ARG_KEY));
        mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                getArguments().getString(Constants.ARG_KEY));
        mBlockPresenter = new BlockPresenter(this);
        mBlockPresenter.checkBlocks(getArguments().getString(Constants.ARG_RECEIVER_UID) + getArguments().getString(Constants.ARG_KEY));
        mTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTextMessage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (keyboardShown(mTextMessage.getRootView())) {
                    if (mChatRecyclerAdapter.getItemCount() > 0) {
                        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
                        //mRecyclerViewChat.scrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
                    }
                }
            }
        });
        ItemClickSupport.addTo(mRecyclerViewChat)
                .setOnItemClickListener(this);
    }

    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }else{
            // we already have permission, lets go ahead and call camera intent
            photoCameraIntent();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StorageReference storageRef = storage.getReferenceFromUrl(Constants.URL_STORAGE_REFERENCE).child(Constants.FOLDER_STORAGE_IMG);

        if (requestCode == IMAGE_GALLERY_REQUEST){
            if (resultCode == RESULT_OK){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    sendFileFirebase(storageRef,selectedImageUri);
                }else{
                    //URI IS NULL
                }
            }
        }else if (requestCode == IMAGE_CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (filePathImageCamera != null && filePathImageCamera.exists()) {
                    StorageReference imageCameraRef = storageRef.child(filePathImageCamera.getName() + "_camera");
                    sendFileFirebase(imageCameraRef, filePathImageCamera);
                } else {
                    //IS NULL
                }
            }
        }
    }

    private void sendFileFirebase(StorageReference storageReference, final File file){
        if (storageReference != null){
            Uri photoURI = FileProvider.getUriForFile(getActivity(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
            UploadTask uploadTask = storageReference.putFile(photoURI);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Upload","onFailure sendFileFirebase "+e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("Upload","onSuccess sendFileFirebase");
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                    FileModel fileModel = new FileModel("img",downloadUrl.toString(),file.getName(),file.length()+"");
//                    ChatModel chatModel = new ChatModel(userModel,"",Calendar.getInstance().getTime().getTime()+"",fileModel);
//                    mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                }
            });
        }else{
            //IS NULL
        }

    }

    private void sendFileFirebase(StorageReference storageReference, final Uri file){
        if (storageReference != null){
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name+"_gallery");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Upload","onFailure sendFileFirebase "+e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("Upload","onSuccess sendFileFirebase");
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                    FileModel fileModel = new FileModel("img",downloadUrl.toString(),name,"");
//                    ChatModel chatModel = new ChatModel(userModel,"", Calendar.getInstance().getTime().getTime()+"",fileModel);
//                    mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                }
            });
        }else{
            //IS NULL
        }

    }

    /**
     * Enviar foto tirada pela camera
     */
    private void photoCameraIntent(){
        String nomeFoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto+"camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".provider",
                filePathImageCamera);
        it.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    photoCameraIntent();
                }
                break;
        }
    }

    /**
     * Enviar foto pela galeria
     */
    private void photoGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Get Photo"), IMAGE_GALLERY_REQUEST);
    }

    private boolean keyboardShown(View rootView) {

        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    private void sendMessage() {
        if (mMyBlocked) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
            //dialogBuilder.setTitle("");
            dialogBuilder.setMessage("You need to unblock in order to send message");
            dialogBuilder.setPositiveButton("UnBlock", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    unBlock();
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            dialogBuilder.show();
            return;
        } else if (mHisBlocked) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
            //dialogBuilder.setTitle("");
            dialogBuilder.setMessage("You can't send message for block");
            dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            dialogBuilder.show();
            return;
        }
        mSendButton.setEnabled(false);
        String key = getArguments().getString(Constants.ARG_KEY);
        String message = mTextMessage.getText().toString();
        Chat chat = new Chat(getArguments().getString(Constants.ARG_RECEIVER_UID),
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                message,
                System.currentTimeMillis(), false);
        mChatPresenter.sendMessage(chat, key);
    }

    @Override
    public void onSendMessageSuccess(String message) {
        mTextMessage.setText("");
        //Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
        mSendButton.setEnabled(true);
        //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>());
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.add(chat);
        if (mChatCount != 0) {
            if (mChatRecyclerAdapter.getItemCount() >= mChatCount) {
                mRecyclerViewChat.scrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
                mChatCount = 0;
            }
        } else {
            mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onChangeMessagesSuccess(Chat chat) {
        mChatRecyclerAdapter.change(chat);
        //mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckMessageSuccess(long count) {
        mChatCount = count;
    }

    @Override
    public void onCheckMessageFailure(String message) {

    }

    @Override
    public void onBlockUserSuccess(String chatRoom) {

    }

    @Override
    public void onBlockUserFailure(String message) {

    }

    @Override
    public void onUnBlockUserSuccess(String chatRoom) {

    }

    @Override
    public void onUnBlockUserFailure(String message) {

    }

    @Override
    public void onBlockAdded(String chatRoom) {
        if (TextUtils.equals(chatRoom, getArguments().getString(Constants.ARG_RECEIVER_UID) + '_' +  getArguments().getString(Constants.ARG_KEY))) {
            mHisBlocked = true;
            ((ChatActivity) getActivity()).setHisBlocked(true);
        } else if (TextUtils.equals(chatRoom, FirebaseAuth.getInstance().getCurrentUser().getUid() + '_' +  getArguments().getString(Constants.ARG_KEY))) {
            mMyBlocked = true;
            ((ChatActivity) getActivity()).setMyBlocked(true);
        }
    }

    @Override
    public void onBlockRemoved(String chatRoom) {
        if (TextUtils.equals(chatRoom, getArguments().getString(Constants.ARG_RECEIVER_UID) + '_' +  getArguments().getString(Constants.ARG_KEY))) {
            mHisBlocked = false;
            ((ChatActivity) getActivity()).setHisBlocked(false);
        } else if (TextUtils.equals(chatRoom, FirebaseAuth.getInstance().getCurrentUser().getUid() + '_' +  getArguments().getString(Constants.ARG_KEY))) {
            mMyBlocked = false;
            ((ChatActivity) getActivity()).setMyBlocked(false);
        }
    }

    @Override
    public void onCheckFailure(String message) {

    }

    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
            mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    pushNotificationEvent.getUid());
        }
    }
}
