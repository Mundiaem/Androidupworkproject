package com.ict2105_team05_2017.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.facebook.internal.FacebookDialogFragment.TAG;

/**
 * Created by Macharia on 2/20/2017.
 */

public class ExtractIdsForJSONIDs {
    public static String extractIdsForFaceBook(JSONObject responseJson) {
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
    public  String extractFBIDS(JSONObject responseJson) {
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
}
