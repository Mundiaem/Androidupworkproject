package com.ict2105_team05_2017.tasks;

import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.ict2105_team05_2017.callbacks.OnLoadUsers;
import com.ict2105_team05_2017.callbacks.OnuserFinishedLoading;
import com.ict2105_team05_2017.model.User;
import com.ict2105_team05_2017.utils.GetUsers;

import java.util.ArrayList;


/**
 * Created by Macharia on 2/21/2017.
 */

public class TaskLoadUsers extends AsyncTask<DatabaseReference, Void, ArrayList<User>> {
    private OnLoadUsers loadUsers;
    private OnuserFinishedLoading onuserFinishedLoading;


    public TaskLoadUsers(OnLoadUsers loadUsers, OnuserFinishedLoading onuserFinishedLoading) {
        this.loadUsers = loadUsers;
        this.onuserFinishedLoading = onuserFinishedLoading;
    }

    @Override
    protected ArrayList<User> doInBackground(DatabaseReference... voids) {
        DatabaseReference mFirebasedatabase = voids[0];
        return GetUsers.getUsersList(mFirebasedatabase, onuserFinishedLoading);
    }

    @Override
    protected void onPostExecute(ArrayList<User> users) {
        super.onPostExecute(users);
        if (loadUsers != null) {
            loadUsers.onGetUsers(users);
        }
    }
}
