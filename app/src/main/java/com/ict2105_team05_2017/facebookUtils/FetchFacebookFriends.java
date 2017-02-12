package com.ict2105_team05_2017.facebookUtils;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import bolts.Task;
import bolts.TaskCompletionSource;

/**
 * Created by Macharia on 2/12/2017.
 */

public class FetchFacebookFriends {
    public static Task<String[]> getUserFriends() {
        final TaskCompletionSource<String[]> fetchFriendsTask = new TaskCompletionSource<>();
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                JSONObject responseJson = response.getJSONObject();

                if (responseJson != null) {
                    String[] ids = extractIdsForJSON(responseJson);
                    if (ids != null && ids.length > 0) {
                        fetchFriendsTask.setResult(ids);

                    } else {
                        fetchFriendsTask.setError(new Exception("Not enough ids for Friends"));
                    }
                } else {
                    fetchFriendsTask.setError(new Exception("Graph response is null"));
                }
            }
        }).executeAsync();
        return fetchFriendsTask.getTask();
    }

    private static String[] extractIdsForJSON(JSONObject responseJson) {
        JSONArray data = responseJson.optJSONArray("data");
        if (data != null) {
            String[] ids = new String[data.length()];
            for (int i = 0; i < data.length(); i++) {
                ids[i] = data.optJSONObject(i).optString("id");
            }
            return ids;
        }
        return null;

    }
}
