package com.ict2105_team05_2017.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.ict2105_team05_2017.R;
import com.ict2105_team05_2017.adapters.FaceBookFriendsAdapter;
import com.ict2105_team05_2017.adapters.UsersTouchAdapter;
import com.ict2105_team05_2017.callbacks.OnLoadUsers;
import com.ict2105_team05_2017.callbacks.OnuserFinishedLoading;
import com.ict2105_team05_2017.model.Friends;
import com.ict2105_team05_2017.model.User;
import com.ict2105_team05_2017.utils.ExtractIdsForJSONIDs;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Macharia on 2/12/2017.
 */

public class CurrentFriendsFragment extends Fragment {
    private static final String TAG = FacebookFriendsFragment.class.getName();
    private static final String STATE_USERS = "state_users";
    private FaceBookFriendsAdapter faceBookFriendsAdapter;
    /*private List<Friends> friendsList = new ArrayList<>();*/
    private ArrayList<User> userList = new ArrayList<>();
    private RecyclerView mRecylerView;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private OnuserFinishedLoading onuserFinishedLoading;
    private OnLoadUsers loadUsers;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.currentfriendsfragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecylerView = (RecyclerView) view.findViewById(R.id.facebookFriesnds);
        mRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAuth = FirebaseAuth.getInstance();

            /*Getting reference to the node*/
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("usersCollection");
        // Storing the app title to the node
        mFirebaseInstance.getReference("app_title").setValue("ict2105team052017");

        //Initializing the FaceBookFriendsAdapter and RecyclerView
        faceBookFriendsAdapter = new FaceBookFriendsAdapter(getContext(), userList, mFirebaseInstance, mFirebaseDatabase, mAuth);
        mRecylerView.setAdapter(faceBookFriendsAdapter);
        getUsers();
        // Setup ItemTouchHelper
        ItemTouchHelper.Callback callback = new UsersTouchAdapter(faceBookFriendsAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecylerView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void getUsers() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String currentUserId = currentUser.getUid();
        Query myUsersQuery = mFirebaseDatabase.child("users_data").child(currentUserId).child("friends");

        // User data change listener
        // My top posts by number of stars

        myUsersQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();

                final Friends friends = dataSnapshot.getValue(Friends.class);
                boolean isAFriend = friends.getFriend();
                boolean accepted = friends.getAcceptedRequest();
                User user = new User();

                if (isAFriend && accepted) {
                    String name = friends.getName();
                    user.setName(name);
                    String email = friends.getEmail();
                    user.setEmail(email);
                    String id = friends.getId();
                    user.setId(id);
                    Friends userFriend = new Friends();
                    userFriend.setAcceptedRequest(true);
                    userFriend.setFriend(true);
                    userFriend.setFriendshipDeclined(false);
                    user.setFriends(userFriend);

                    if (friends.getFbId() != null && !friends.getFbId().isEmpty()) {
                        String fbId = friends.getFbId();
                        user.setFacebookId(fbId);
                    }
                    if (friends.getAfriendFromFb()) {

                        Friends fbFrd = new Friends();
                        fbFrd.setAfriendFromFb(true);
                        user.setFriends(fbFrd);
                    } else {
                        Friends fbFrd = new Friends();
                        fbFrd.setAfriendFromFb(false);
                        user.setFriends(fbFrd);
                    }

                    userList.add(user);
                    faceBookFriendsAdapter.updateFriends(userList);

                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                String commentKey = dataSnapshot.getKey();


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
