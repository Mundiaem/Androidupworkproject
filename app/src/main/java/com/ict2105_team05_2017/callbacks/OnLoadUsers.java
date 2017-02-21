package com.ict2105_team05_2017.callbacks;

import com.ict2105_team05_2017.model.User;

import java.util.ArrayList;


/**
 * Created by Macharia on 2/21/2017.
 */

public interface OnLoadUsers {
    void onGetUsers(ArrayList<User> userList);
}
