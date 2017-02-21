package com.ict2105_team05_2017.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Macharia on 2/16/2017.
 */

public class UsersTouchAdapter extends ItemTouchHelper.SimpleCallback{
    private FaceBookFriendsAdapter mFriendsAdapter;

    public UsersTouchAdapter(FaceBookFriendsAdapter mFriendsAdapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mFriendsAdapter = mFriendsAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mFriendsAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mFriendsAdapter.remove(viewHolder.getAdapterPosition());
    }
}
