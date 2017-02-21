package com.ict2105_team05_2017.utils;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ict2105_team05_2017.callbacks.OnLoadUsers;
import com.ict2105_team05_2017.callbacks.OnuserFinishedLoading;
import com.ict2105_team05_2017.model.Friends;
import com.ict2105_team05_2017.model.User;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static com.ict2105_team05_2017.utils.ExtractIdsForJSONIDs.extractIdsForFaceBook;

/**
 * Created by Macharia on 2/20/2017.
 */

public class GetUsers {
    private static final String TAG = GetUsers.class.getSimpleName();
    private static ArrayList<User> userList = new ArrayList<>();

    public static ArrayList<User> getUsersList(DatabaseReference mFirebaseDatabse, OnuserFinishedLoading loadUsers) {
        Query myUsersQuery = mFirebaseDatabse.child("users_data");
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

                    if (user.getFacebookId() != null && !user.getFacebookId().isEmpty()) {
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
                                        String userFBId = extractIdsForFaceBook(responseJSON);

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


                                    } else {
                                        Log.e(TAG, "Object is null");

                                    }
        /* handle the result */
                                }
                        ).executeAsync();


                    } else {
                        Log.e(TAG, "This is the Id From FB !! NULL");
                        Friends friend = new Friends();
                        friend.setAfriendFromFb(false);
                        user.setFriends(friend);
                        userList.add(user);

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

        Log.e(TAG, "THis is called at this moment Check it out!" + userList);
        return userList;
    }
}
