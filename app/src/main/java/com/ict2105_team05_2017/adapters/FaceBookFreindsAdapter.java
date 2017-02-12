package com.ict2105_team05_2017.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ict2105_team05_2017.R;
import com.ict2105_team05_2017.fragments.FacebookFriendsFragment;
import com.ict2105_team05_2017.model.Friends;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Macharia on 2/12/2017.
 */

public class FaceBookFreindsAdapter extends RecyclerView.Adapter<FaceBookFreindsAdapter.ViewHolder> {
    private static final String TAG= FaceBookFreindsAdapter.class.getName();
    private Context mContext;
    private List<Friends> mFriendList = new ArrayList<>();

    public FaceBookFreindsAdapter(Context context, List<Friends> friendsList) {
        this.mContext = context;
        this.mFriendList = friendsList;
    }

    @Override
    public FaceBookFreindsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.fetchfriendsfragment, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FaceBookFreindsAdapter.ViewHolder holder, int position) {
        Friends friend = mFriendList.get(position);
        Log.e(TAG, friend.getName()+" " +friend.getId());

        renderingView(friend, holder);


    }

    private FaceBookFreindsAdapter.ViewHolder renderingView(Friends friend, ViewHolder holder) {
        holder.name.setText(friend.getName());
        return holder;

    }

    @Override
    public int getItemCount() {

        return mFriendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Calling the and initializing the views
        private ImageView profilePick;
        private TextView name;
        private TextView timestamp;
        private TextView statusMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            profilePick = (ImageView) itemView.findViewById(R.id.profilePic);
            name = (TextView) itemView.findViewById(R.id.name);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            statusMessage = (TextView) itemView.findViewById(R.id.txtStatusMsg);
        }
    }
}
