package com.ict2105_team05_2017.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ict2105_team05_2017.R;
import com.ict2105_team05_2017.adapters.FaceBookFreindsAdapter;
import com.ict2105_team05_2017.model.Friends;
import com.ict2105_team05_2017.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Macharia on 2/12/2017.
 */

public class FacebookFriendsFragment extends Fragment {
    private static final String TAG = FacebookFriendsFragment.class.getName();
    private FaceBookFreindsAdapter faceBookFreindsAdapter;
    /*private List<Friends> friendsList = new ArrayList<>();*/
    private List<User> userList = new ArrayList<>();
    private RecyclerView mRecylerView;
    private FirebaseAuth mAuth;


    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;


    private static String extractIdsForJSONIDs(JSONObject responseJson) {
        String ids = null;
        JSONArray data = responseJson.optJSONArray("data");
        if (data != null) {
            for (int i = 0; i < data.length(); i++) {
                ids = data.optJSONObject(i).optString("id");
                Log.e(TAG, "This is the fb Id!! " + ids);
            }
            return ids;
        }
        return null;
    }

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
        linkingIds(mFirebaseInstance, mFirebaseDatabase);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void linkingIds(FirebaseDatabase mFirebaseInstance, DatabaseReference mFirebaseDatabase) {
        Query myUsersQuery = mFirebaseDatabase.child("users_data");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUserId = firebaseUser.getUid();
        // User data change listener
        // My top posts by number of stars
        myUsersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    String key = postSnapshot.getKey();

                    final User user = postSnapshot.getValue(User.class);
                    String name = user.getName();
                    String id = user.getId();
                    if (!Objects.equals(currentUserId, id)) {
                        Log.e(TAG, "This is the USer Id!! Current User " + id + name);


                        ///Getting friends From FaceBook
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
                                        String userFBId = extractIdsForJSONIDs(responseJSON);

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
                                        faceBookFreindsAdapter = new FaceBookFreindsAdapter(getContext(), userList, FacebookFriendsFragment.this.mFirebaseInstance, FacebookFriendsFragment.this.mFirebaseDatabase);
                                        mRecylerView.setAdapter(faceBookFreindsAdapter);
                                        faceBookFreindsAdapter.notifyDataSetChanged();


                                    } else {
                                        Log.e(TAG, "Object is null");

                                    }
        /* handle the result */
                                }
                        ).executeAsync();

                    } else {
                        Log.e(TAG, "This is the USer Id!! " + id + name);

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


}