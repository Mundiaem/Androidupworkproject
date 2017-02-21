package com.ict2105_team05_2017.facebook;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.ict2105_team05_2017.callbacks.IsFaceBookFriend;
import com.ict2105_team05_2017.utils.ExtractIdsForJSONIDs;

import org.json.JSONObject;

/**
 * Created by Macharia on 2/20/2017.
 */

public class GetFacebookFriends extends AsyncTask<Void, Void, Boolean> {
    private final String TAG = GetFacebookFriends.class.getSimpleName();
    GraphResponse graphResponse;
    private String userId;
    private IsFaceBookFriend faceBookFriend;

    public GetFacebookFriends(String id, IsFaceBookFriend isFaceBookFriend) {
        this.userId = id;
        faceBookFriend = isFaceBookFriend;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean isFacebookFried = false;

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                response -> {
                    JSONObject responseJSON = response.getJSONObject();
                    if (responseJSON != null) {
                        graphResponse = response;

                    } else {
                        Log.e(TAG, "Object is null");

                    }
        /* handle the result */
                }
        ).executeAsync();
        if (graphResponse != null) {

            JSONObject JSONResponse = graphResponse.getJSONObject();
            ExtractIdsForJSONIDs faceBookIds = new ExtractIdsForJSONIDs();
            String id = faceBookIds.extractIdsForFaceBook(JSONResponse);
            if (userId.equals(id)) {
                isFacebookFried = true;
            } else {
                isFacebookFried = false;
            }
        } else {
            Log.e(TAG, "GraphResponse Object is null");

        }
        return isFacebookFried;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        faceBookFriend.isAFriend(aBoolean);
        super.onPostExecute(aBoolean);
    }
}
