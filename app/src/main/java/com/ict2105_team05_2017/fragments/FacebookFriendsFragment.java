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
import java.util.Objects;

/**
 * Created by Macharia on 2/12/2017.
 */

public class FacebookFriendsFragment extends Fragment implements OnLoadUsers, OnuserFinishedLoading {
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fetchfriendsfragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecylerView = (RecyclerView) view.findViewById(R.id.facebookFriends_RecyclerView);
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

        //Checking if Parcelable is empty
        if (savedInstanceState != null) {
            userList = savedInstanceState.getParcelableArrayList(STATE_USERS);
            faceBookFriendsAdapter.updateFriends(userList);

        } else {
            linkingIds(mFirebaseDatabase);
            faceBookFriendsAdapter.updateFriends(userList);

            Log.e(TAG, "This should load Now!");
        }


        // Setup ItemTouchHelper
        ItemTouchHelper.Callback callback = new UsersTouchAdapter(faceBookFriendsAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecylerView);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the users list to a parcelable prior to the rotation or configuration change
        outState.putParcelableArrayList(STATE_USERS, userList);

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void linkingIds(DatabaseReference mFirebaseDatabase) {
        Query myUsersQuery = mFirebaseDatabase.child("users_data");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUserId = firebaseUser.getUid();

        // User data change listener
        // My top posts by number of stars

        myUsersQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();

                final User user = dataSnapshot.getValue(User.class);
                String name = user.getName();
                String id = user.getId();


                if (!Objects.equals(currentUserId, id) && user.getId() != null && !user.getId().isEmpty() && user.getName() != null && !user.getName().isEmpty()) {
                    Log.e(TAG, "This is the USer Id!! Current User " + id + name);
               Log.e(TAG,  "Get the Provider data: !! " +firebaseUser.getProviderId());
                    ///Getting friends From FaceBook
                  if (AccessToken.getCurrentAccessToken() != null){
                      new GraphRequest(
                              AccessToken.getCurrentAccessToken(),
                              "/me/friends",
                              null,
                              HttpMethod.GET,
                              response -> {
                                  JSONObject responseJSON = response.getJSONObject();
                                  if (responseJSON != null) {
                                      Log.e(TAG, responseJSON.toString());

                                      //Querying the USer
                                      String fbId = user.getFacebookId();
                                      Log.e(TAG, "This is the Id From FB " + fbId);
                                      String userFBId = ExtractIdsForJSONIDs.extractIdsForFaceBook(responseJSON);

                                      if (Objects.equals(fbId, userFBId)) {
                                          Log.e(TAG, "This is the Id From FB !! is True " + userFBId + " " + fbId);
                                          Friends friend = new Friends();
                                          friend.setAfriendFromFb(true);
                                          user.setFriends(friend);

                                      } else {
                                          Log.e(TAG, "This is the Id From FB !! is False" + userFBId + " " + fbId);
                                          Friends friend = new Friends();
                                          friend.setAfriendFromFb(false);
                                          user.setFriends(friend);

                                      }
                                      userList.add(user);
                                      faceBookFriendsAdapter.updateFriends(userList);
                                  } else {
                                      Log.e(TAG, "Object is null");

                                  }
        /* handle the result */
                              }
                      ).executeAsync();
                  }else {
                      Friends friend = new Friends();
                      friend.setAfriendFromFb(false);
                      user.setFriends(friend);
                      userList.add(user);
                      faceBookFriendsAdapter.updateFriends(userList);

                  }




                } else {
                    Log.e(TAG, "This is the USer Id!! " + id + name);

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


    @Override
    public void onGetUsers(ArrayList<User> userList) {
        Log.e(TAG, "The callback is called " + userList);
        faceBookFriendsAdapter.updateFriends(userList);
        faceBookFriendsAdapter.notifyDataSetChanged();
    }

    @Override
    public void userFinishedLoading(ArrayList<User> userArrayList) {
        Log.e(TAG, "The userFinishedLoading callback is called " + userArrayList);
        faceBookFriendsAdapter.updateFriends(userList);
        faceBookFriendsAdapter.notifyDataSetChanged();
    }
}