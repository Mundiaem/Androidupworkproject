package com.ict2105_team05_2017.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ict2105_team05_2017.R;
import com.ict2105_team05_2017.activities.LoginActivity;
import com.ict2105_team05_2017.adapters.FaceBookFreindsAdapter;
import com.ict2105_team05_2017.facebookUtils.FetchFacebookFriends;
import com.ict2105_team05_2017.fragments.CurrentFriendsFragment;
import com.ict2105_team05_2017.fragments.FacebookFriendsFragment;
import com.ict2105_team05_2017.model.Friends;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private FragmentManager manager;
    private ProgressDialog mProgressDialog;

    private static Friends extractIdsForJSON(JSONObject responseJson) {
        JSONArray data = responseJson.optJSONArray("data");
        Friends friendList;
        if (data != null) {
            String[] ids = new String[data.length()];
            for (int i = 0; i < data.length(); i++) {
                ids[i] = data.optJSONObject(i).optString("id");
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = getSupportFragmentManager();
        FacebookFriendsFragment FacebookFriendsFragmentFragment = new FacebookFriendsFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, FacebookFriendsFragmentFragment, " FacebookFriendsFragmentFragment");
        transaction.commit();


    }



    private void onPhotoReady(final Uri pathToImage) {
        mProgressDialog = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.please_wait));
        FetchFacebookFriends.getUserFriends()
                .onSuccessTask(new Continuation<String[], Task<List<Friends>>>() {
                    @Override
                    public Task<List<Friends>> then(Task<String[]> task) throws Exception {
                        /*return ParseFacebookUtils.fetchFriendForId(task.getResult());*/
                        return null;
                    }
                })
                .onSuccessTask(new Continuation<List<Friends>, Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<List<Friends>> task) throws Exception {
                        /*savePhoto(pathToImage, task.getResult());*/
                        return null;
                    }
                })
                .continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(Task<Void> task) throws Exception {
                        if (task.getError() != null)
                            task.getError().printStackTrace();

                        if (mProgressDialog != null)
                            mProgressDialog.cancel();

                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
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

}
