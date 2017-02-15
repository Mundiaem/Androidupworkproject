package com.ict2105_team05_2017.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ict2105_team05_2017.R;
import com.ict2105_team05_2017.fragments.CurrentFriendsFragment;
import com.ict2105_team05_2017.fragments.FacebookFriendsFragment;
import com.ict2105_team05_2017.utils.SendingNotification;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private FragmentManager manager;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
          /*Getting reference to the node*/
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("usersCollection");
        // Storing the app title to the node
        mFirebaseInstance.getReference("app_title").setValue("ict2105team052017");

        manager = getSupportFragmentManager();
        FacebookFriendsFragment FacebookFriendsFragmentFragment = new FacebookFriendsFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, FacebookFriendsFragmentFragment, " FacebookFriendsFragmentFragment");
        transaction.commit();


    }

    @Override
    protected void onStart() {
        super.onStart();
        updateFirebaseToken();
    }

    private void onSignOutSuccess() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_facebookFriends:
                FacebookFriendsFragment FacebookFriendsFragmentFragment1 = new FacebookFriendsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, FacebookFriendsFragmentFragment1, " FacebookFriendsFragmentFragment").commit();
                break;
            case R.id.action_currentFriends:
                CurrentFriendsFragment addFragment = new CurrentFriendsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, addFragment, "addFragment").commit();
                break;
            case R.id.action_logout:
                logOut();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            onSignOutSuccess();
        }
    }
    private void updateFirebaseToken() {
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "This is the Token: " + refreshToken);
        assert currentUser != null;
        mFirebaseDatabase.child("users_data").child(currentUser.getUid()).child("token").setValue(refreshToken);
    }

}
