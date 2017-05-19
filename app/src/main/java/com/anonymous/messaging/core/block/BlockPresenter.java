package com.anonymous.messaging.core.block;

/**
 * Author: Kartik Sharma
 * Created on: 9/2/2016 , 10:05 PM
 * Project: FirebaseChat
 */

public class BlockPresenter implements BlockContract.Presenter, BlockContract.OnContactDatabaseListener {
    private BlockContract.View mView;
    private BlockInteractor mBlockInteractor;

    public BlockPresenter(BlockContract.View view) {
        this.mView = view;
        mBlockInteractor = new BlockInteractor(this);
    }

    @Override
    public void blockUser(String chatRoom) {
        mBlockInteractor.blockUser(chatRoom);
    }

    @Override
    public void unBlockUser(String chatRoom) {
        mBlockInteractor.unBlockUser(chatRoom);
    }

    @Override
    public void checkBlocks(String chatRoom) {
        mBlockInteractor.checkBlocks(chatRoom);
    }

    @Override
    public void onBlockUserSuccess(String chatRoom) {
        mView.onBlockUserSuccess(chatRoom);
    }

    @Override
    public void onBlockUserFailure(String message) {
        mView.onBlockUserFailure(message);
    }

    @Override
    public void onUnBlockUserSuccess(String chatRoom) {
        mView.onUnBlockUserSuccess(chatRoom);
    }

    @Override
    public void onUnBlockUserFailure(String message) {
        mView.onUnBlockUserFailure(message);
    }

    @Override
    public void onBlockAdded(String chatRoom) {
        mView.onBlockAdded(chatRoom);
    }

    @Override
    public void onBlockRemoved(String chatRoom) {
        mView.onBlockRemoved(chatRoom);
    }

    @Override
    public void onCheckFailure(String message) {
        mView.onCheckFailure(message);
    }
}
