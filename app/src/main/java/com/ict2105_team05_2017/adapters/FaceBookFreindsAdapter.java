package com.ict2105_team05_2017.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
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
import com.ict2105_team05_2017.model.Friends;
import com.ict2105_team05_2017.model.User;
import com.ict2105_team05_2017.utils.SendingNotification;

import org.json.JSONArray;

import java.util.ArrayList;
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
    private List<User> friendsList = new ArrayList<>();


    public FaceBookFreindsAdapter(Context context, List<User> mUsersList, FirebaseDatabase mfirebaseInstance, DatabaseReference mDatabase) {
        this.mContext = context;
        this.friendsList = mUsersList;
        this.mfirebaseInstance = mfirebaseInstance;
        this.mDatabase = mDatabase;
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
        holder.profilePick.setProfileId(mUser.getFacebookId());

        if (mUser.getFriends().getAfriendFromFb()) {
            holder.isFromFacebook.setText("Facebook Friend");

        } else {
            holder.isFromFacebook.setVisibility(View.GONE);
        }
        holder.sendRequest.setOnClickListener(view -> {
            Log.e(TAG, "This is the faceBook Id: " + mUser.getId());
            sendmUserRequest(mUser);


        });
        assert firebaseUser != null;

        //Sending, Accepting and Rejecting Friends Requests


        renderingView(mUser, holder, firebaseUser);


    }


    private void renderingView(final User mUser, final ViewHolder holder, FirebaseUser firebaseUser) {
        Query myUsersQuery = mDatabase.child("users_data").child(firebaseUser.getUid()).child("friends");

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
        boolean isChanged = insertToCurrentUser(firebaseUser, user);
        boolean sentFriendRequest = mDatabase.child("users_data").child(user.getId()).child("friends").child(firebaseUser.getUid()).setValue(potentialFriend).isSuccessful();
        if (isChanged && sentFriendRequest) {
            String name = user.getName();
            String body = mContext.getString(R.string.notif_message_sending) + "\"" + firebaseUser.getDisplayName() + " has sent your Friend Request" + "\"";
            String msg = "Check out new Friend Request";
            String token = user.getToken();
            String title = "New Friend Request";
            String icon = "no String Icons";
            sendNotifications(token, msg, title, body, icon, name);
        } else {
            String msg = "Unknown Error occurred";
            showErrorMessage(msg);
        }

    }


    private boolean insertToCurrentUser(final FirebaseUser user, User user1) {
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
        isAsuccess = mDatabase.child("users_data").child(user.getUid()).child("friends").child(user1.getId()).setValue(potentialFriend).isSuccessful();

        return isAsuccess;
    }


    /*Accepting Friend Request*/
    private void acceptingFriendRequest(User user) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        //Current User is Accepting The Friend Request

        boolean accepting = mDatabase.child("users_data").child(currentUser.getUid()).child("friends").child(user.getId()).child("acceptedRequest").setValue(true).isSuccessful();
        boolean friend = mDatabase.child("users_data").child(currentUser.getUid()).child("friends").child(user.getId()).child("friend").setValue(true).isSuccessful();
        //Updating Friend's FriendShip status Status
        boolean updatingFriendStatus = mDatabase.child("users_data").child(user.getId()).child("friends").child(currentUser.getUid()).child("acceptedRequest").setValue(true).isSuccessful();
        boolean isAfriend = mDatabase.child("users_data").child(user.getId()).child("friends").child(currentUser.getUid()).child("friend").setValue(true).isSuccessful();
        if (accepting && friend && updatingFriendStatus && isAfriend) {
            String name = user.getName();
            String body = mContext.getString(R.string.notif_message_accpting) + "\"" + currentUser.getDisplayName() + " has accepted your Friend Request" + "\"";
            String msg = "Check out new Friend";
            String token = user.getToken();
            String title = "New Friend";
            String icon = "no String Icons";
            sendNotifications(token, msg, title, body, icon, name);
        } else {
            String msg = "Unknown Error occurred";
            showErrorMessage(msg);
        }


    }

    /*Declining Friend Requests*/
    private void decliningFriendRequest(User user) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        boolean declinedRequest = mDatabase.child("users_data").child(currentUser.getUid()).child("friends").child(user.getId()).child("friendshipDeclined").setValue(true).isSuccessful();
        boolean updatingPotentialFriend = mDatabase.child("users_data").child(user.getId()).child("friends").child(currentUser.getUid()).child("friendshipDeclined").setValue(true).isSuccessful();
        if (declinedRequest && updatingPotentialFriend) {
            String name = user.getName();
            String body = mContext.getString(R.string.notif_message_declining) + "\"" + currentUser.getDisplayName() + " has declined your Friend Request" + "\"";
            String msg = "Friendship declined";
            String token = user.getToken();
            String title = "New Message";
            String icon = "no String Icons";
            sendNotifications(token, msg, title, body, icon, name);
        } else {
            String msg = "Unknown Error occurred";
            showErrorMessage(msg);
        }
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


        }
    }
}
