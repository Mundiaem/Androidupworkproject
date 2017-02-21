package com.ict2105_team05_2017.facebook;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ict2105_team05_2017.model.Friends;
import com.ict2105_team05_2017.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import static com.facebook.internal.FacebookDialogFragment.TAG;

/**
 * Created by Macharia on 2/20/2017.
 */

public class FacebookUtils {
    private FirebaseAuth mAuth;

    static String extractIdsForJSONIDs(JSONObject responseJson) {
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

    ///Getting friends From FaceBook
    public void faceBookFriends(User user) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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


                    } else {
                        Log.e(TAG, "Object is null");

                    }
        /* handle the result */
                }
        ).executeAsync();

    }
}
