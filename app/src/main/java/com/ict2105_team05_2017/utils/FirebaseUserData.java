package com.ict2105_team05_2017.utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ict2105_team05_2017.model.User;

/**
 * Created by Macharia on 2/12/2017.
 */

public class FirebaseUserData {
    private static final String TAG = FirebaseUserData.class.getName();
    private FirebaseDatabase mfirebaseInstance;
    private DatabaseReference mDatabase;
    private Context mContext;
    private String userId;
    private User mUser;

    public FirebaseUserData(Context context, FirebaseDatabase mFirebaseDatabase, DatabaseReference mDatabase, User user) {
        this.mContext = context;
        this.mfirebaseInstance = mFirebaseDatabase;
        this.mDatabase = mDatabase;
        this.mUser = user;
    }

    /*Create New User*/
    public void CreateUser() {

       userId=mUser.getId();
        // Checking if the User Exist in the database
        mDatabase.child("users_data").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                //Checking for null
                if (user == null) {
                    mDatabase.child("users_data").child(userId).setValue(mUser);
                    addUserChangeListener();
                    Log.e(TAG, "User data is null!");
                    return;
                }
                Log.e(TAG, "User data has changed! " + user.getName() + " " + user.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    /*
    * User data Change listener
    * */
    private void addUserChangeListener() {
        mDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                //Checking for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }
                Log.e(TAG, "User data has changed! " + user.getName() + " " + user.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
