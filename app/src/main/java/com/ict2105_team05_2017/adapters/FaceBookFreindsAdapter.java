package com.ict2105_team05_2017.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ict2105_team05_2017.R;
import com.ict2105_team05_2017.model.Dismissed;
import com.ict2105_team05_2017.model.Friends;
import com.ict2105_team05_2017.model.User;
import com.ict2105_team05_2017.utils.SendingNotification;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by Macharia on 2/12/2017.
 */

public class FaceBookFreindsAdapter extends RecyclerView.Adapter<FaceBookFreindsAdapter.ViewHolder> {
    private static final String TAG = FaceBookFreindsAdapter.class.getName();
    private Context mContext;

    private FirebaseDatabase mfirebaseInstance;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private List<User> friendsList = new ArrayList<>();


    public FaceBookFreindsAdapter(Context context, List<User> mUsersList, FirebaseDatabase mfirebaseInstance, DatabaseReference mDatabase, FirebaseAuth mAuth) {
        this.mContext = context;
        updateFriends(mUsersList);
        this.mfirebaseInstance = mfirebaseInstance;
        this.mDatabase = mDatabase;
        this.mAuth = mAuth;

    }

    public void updateFriends(List<User> mFriendsList) {
        this.friendsList = mFriendsList;
        notifyDataSetChanged();
    }

    @Override
    public FaceBookFreindsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.facebook_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FaceBookFreindsAdapter.ViewHolder holder, int position) {
        final User mUser = friendsList.get(position);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        Log.e(TAG, mUser.getName() + " " + mUser.getId());
        holder.name.setText(mUser.getName());
        if (mUser.getFacebookId() != null) {
            holder.profilePick.setProfileId(mUser.getFacebookId());
        }

        if (mUser.getFriends().getAfriendFromFb()) {
            holder.isFromFacebook.setText("Facebook Friend");

        } else {
            holder.isFromFacebook.setVisibility(View.GONE);
        }
        holder.sendRequest.setText("Send Request");
        holder.acceptRequest.setVisibility(View.GONE);
        holder.rejectRequest.setVisibility(View.GONE);


        holder.sendRequest.setOnClickListener(view -> {
            Log.e(TAG, "This is the faceBook Id: " + mUser.getId());
            sendmUserRequest(mUser);


        });

        assert firebaseUser != null;

        //Sending, Accepting and Rejecting Friends Requests


        renderingView(mUser, holder, firebaseUser);
        mDatabase.child("users_data").child(firebaseUser.getUid()).child("dismissed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void renderingView(final User mUser, final ViewHolder holder, FirebaseUser firebaseUser) {
        Query myUsersQuery = mDatabase.child("users_data").child(firebaseUser.getUid()).child("friends");
        List<Friends> friendsList = mUser.getFriendsList();
     /*   for (int i=0; i< friendsList.size(); i++){
            Friends friendUser=  friendsList.get(i);
            boolean declined= friendUser.getFriendshipDeclined();


        }*/

        myUsersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // TODO: handle the post

                    String id = (String) userSnapshot.child("id").getValue();

                    if (Objects.equals(mUser.getId(), id)) {


                        // Checking Wheather they are equals

                        boolean acccept = (Boolean) userSnapshot.child("acceptedRequest").getValue();
                        boolean decline = (Boolean) userSnapshot.child("friendshipDeclined").getValue();
                        boolean isFreind = (Boolean) userSnapshot.child("friend").getValue();
                        boolean sentFriendRequest = (Boolean) userSnapshot.child("sentFriendRequest").getValue();
                        String name = (String) userSnapshot.child("name").getValue();


                        if (acccept && !decline && isFreind) {
                            //Accepted FriendShip
                            Log.e(TAG, "Accepted FriendShip " + name);

                            holder.actionButtons.setVisibility(View.GONE);
                            holder.friendship_status.setText("Friend");


                        } else if (!sentFriendRequest && !acccept && !decline) {
                            // User has Friend Request
                            Log.e(TAG, "User has Friend Request " + name);
                            holder.friendship_status.setVisibility(View.GONE);
                            holder.sendRequest.setVisibility(View.GONE);
                            holder.rejectRequest.setText("Decline Request");
                            holder.rejectRequest.setOnClickListener(view -> decliningFriendRequest(mUser));
                            holder.acceptRequest.setText("Accept Friend Request");
                            holder.acceptRequest.setOnClickListener(view -> acceptingFriendRequest(mUser));


                        } else if (sentFriendRequest && !acccept && !decline) {
                            //Sent Friend Request
                            Log.e(TAG, "Sent Friend Request " + name);
                            holder.friendship_status.setVisibility(View.GONE);
                            holder.sentFriendRequest.setText("Sent Friend Request");
                            holder.actionButtons.setVisibility(View.GONE);


                        } else if (sentFriendRequest && !acccept && decline) {
                            //Rejected FriendShip
                            Log.e(TAG, "Rejected FriendShip " + name);
                            holder.actionButtons.setVisibility(View.GONE);
                            holder.friendship_status.setText("Declined Friend Request");


                        } else if (!sentFriendRequest && !acccept && decline) {
                            //User Rejected Friend Request
                            Log.e(TAG, "User Rejected Friend Request " + name);
                            holder.actionButtons.setVisibility(View.GONE);
                            holder.friendship_status.setText("You Declined");
                        }
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }


    @Override
    public int getItemCount() {

        return friendsList.size();
    }

    public void sendmUserRequest(final User user) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Friends potentialFriend = new Friends();
        potentialFriend.setId(firebaseUser.getUid());
        assert firebaseUser != null;
        potentialFriend.setName(firebaseUser.getDisplayName());
        potentialFriend.setEmail(firebaseUser.getEmail());
        potentialFriend.setAcceptedRequest(false);

        if (user.getFriends().getAfriendFromFb()) {
            potentialFriend.setAfriendFromFb(true);
        } else {
            potentialFriend.setAfriendFromFb(false);
        }
        potentialFriend.setSentFriendRequest(false);
        potentialFriend.setFriend(false);
        potentialFriend.setFriendshipDeclined(false);
        insertToCurrentUser(firebaseUser, user);
        mDatabase.child("users_data").child(user.getId()).child("friends").child(firebaseUser.getUid()).setValue(potentialFriend);

        String name = user.getName();
        String body = firebaseUser.getDisplayName() + "  has sent your Friend Request" + "\"";
        String msg = "Check out new Friend Request";
        String token = user.getToken();
        String title = "New Friend Request";
        String icon = "no String Icons";
        sendNotifications(token, msg, title, body, icon, name);


    }


    private void insertToCurrentUser(final FirebaseUser user, User user1) {
        Boolean isAsuccess;
        // Get user information
        Friends potentialFriend = new Friends();
        potentialFriend.setId(user1.getId());
        potentialFriend.setName(user1.getName());
        potentialFriend.setEmail(user1.getEmail());
        potentialFriend.setAcceptedRequest(false);
        if (user1.getFriends().getAfriendFromFb()) {
            potentialFriend.setAfriendFromFb(true);
        } else {
            potentialFriend.setAfriendFromFb(false);
        }
        potentialFriend.setSentFriendRequest(true);
        potentialFriend.setFriend(false);
        potentialFriend.setFriendshipDeclined(false);
        mDatabase.child("users_data").child(user.getUid()).child("friends").child(user1.getId()).setValue(potentialFriend);


    }


    /*Accepting Friend Request*/
    private void acceptingFriendRequest(User user) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        //Current User is Accepting The Friend Request

        mDatabase.child("users_data").child(currentUser.getUid()).child("friends").child(user.getId()).child("acceptedRequest").setValue(true);
        mDatabase.child("users_data").child(currentUser.getUid()).child("friends").child(user.getId()).child("friend").setValue(true);
        //Updating Friend's FriendShip status Status
        mDatabase.child("users_data").child(user.getId()).child("friends").child(currentUser.getUid()).child("acceptedRequest").setValue(true);
        mDatabase.child("users_data").child(user.getId()).child("friends").child(currentUser.getUid()).child("friend").setValue(true);

        String name = user.getName();
        String body = mContext.getString(R.string.notif_message_accpting) + "\"" + currentUser.getDisplayName() + " has accepted your Friend Request" + "\"";
        String msg = "Check out new Friend";
        String token = user.getToken();
        String title = "New Friend";
        String icon = "no String Icons";
        sendNotifications(token, msg, title, body, icon, name);


    }

    /*Declining Friend Requests*/
    private void decliningFriendRequest(User user) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("users_data").child(currentUser.getUid()).child("friends").child(user.getId()).child("friendshipDeclined").setValue(true);
        mDatabase.child("users_data").child(user.getId()).child("friends").child(currentUser.getUid()).child("friendshipDeclined").setValue(true);

        String name = user.getName();
        String body = mContext.getString(R.string.notif_message_declining) + "\"" + currentUser.getDisplayName() + " has declined your Friend Request" + "\"";
        String msg = "Friendship declined";
        String token = user.getToken();
        String title = "New Message";
        String icon = "no String Icons";
        sendNotifications(token, msg, title, body, icon, name);
    }

    //Checking if The user is Dismissed
    private void isDismissed(User user, int position) {
        mDatabase.child("users_data").child(mAuth.getCurrentUser().getUid()).child("dismissed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dismissedList : dataSnapshot.getChildren()) {


                    String id = (String) dismissedList.child("id").getValue();
                    String name = (String) dismissedList.child("name").getValue();
                    if (id != null && name != null) {
                        if (id.contains(user.getId())) {
                            if (position == friendsList.size() - 1) {
                                friendsList.remove(position);
                                notifyItemChanged(position);
                                notifyItemChanged(position, friendsList.size());
                            } else {
                                int shift = 1;
                                while (true) {
                                    try {
                                        friendsList.remove(position - shift);
                                        notifyItemChanged(position);
                                        notifyItemChanged(position, friendsList.size());
                                    } catch (IndexOutOfBoundsException e) {
                                        shift++;
                                    }
                                }
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /*Not interested is for dismissing users whom current user is not interested
    * in their friendship*/
    private void notInterested(User user) {
        FirebaseUser author = mAuth.getCurrentUser();
        Dismissed dismissed = new Dismissed();
        dismissed.setCleared(true);
        dismissed.setId(user.getId());
        dismissed.setName(user.getName());
        mDatabase.child("users_data").child(author.getUid()).child("dismissed").child(user.getId()).setValue(dismissed);

        Log.e(TAG, "Sent Friend Request notInterested " + user.getId());


    }

    //SendingNotification
    private void sendNotifications(String token, String msg, String title, String body, String icon, String name) {
        SendingNotification notification = new SendingNotification(mContext);
        List<String> ids = new ArrayList<>();
        ids.add(token);
        JSONArray jsonArray = new JSONArray(ids);
        notification.sendMessage(jsonArray, title, body, icon, msg, name);
    }

    private void showErrorMessage(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private String getDismissedIds(User user) {
        FirebaseUser author = mAuth.getCurrentUser();
        String key = mDatabase.child("users_data").child(author.getUid()).child("dismissed").child(user.getId()).getKey();
        return key;

    }


    public void remove(int position) {
        try{
            User userToDismiss = friendsList.get(position);
            notInterested(userToDismiss);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

        friendsList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();

    }

    public void swap(int firstPosition, int secondPosition) {
        Collections.swap(friendsList, firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Calling the and initializing the views
        ProfilePictureView profilePick;
        private TextView name;
        private TextView isFromFacebook;
        private TextView sendRequest;
        private TextView acceptRequest;
        private TextView rejectRequest;
        private TextView friendship_status;
        private LinearLayout actionButtons;
        private TextView sentFriendRequest;
        private TextView dismiss;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            profilePick = (ProfilePictureView) itemView.findViewById(R.id.profilePic);
            name = (TextView) itemView.findViewById(R.id.friend_name);
            isFromFacebook = (TextView) itemView.findViewById(R.id.isFromFacebook);
            sendRequest = (TextView) itemView.findViewById(R.id.txtUrl);
            acceptRequest = (TextView) itemView.findViewById(R.id.acceptRequest);
            rejectRequest = (TextView) itemView.findViewById(R.id.rejectRequest);
            friendship_status = (TextView) itemView.findViewById(R.id.friendship_status);
            actionButtons = (LinearLayout) itemView.findViewById(R.id.actionButtons);
            sentFriendRequest = (TextView) itemView.findViewById(R.id.sentFriendRequest);
            dismiss = (TextView) itemView.findViewById(R.id.dismiss);
            cardView = (CardView) itemView.findViewById(R.id.friend_list);


        }
    }
}
