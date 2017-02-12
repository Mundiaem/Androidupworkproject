package com.ict2105_team05_2017.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.ict2105_team05_2017.R;
import com.ict2105_team05_2017.adapters.FaceBookFreindsAdapter;
import com.ict2105_team05_2017.model.Friends;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Macharia on 2/12/2017.
 */

public class FacebookFriendsFragment extends Fragment {
    private static final String TAG = FacebookFriendsFragment.class.getName();
    private FaceBookFreindsAdapter faceBookFreindsAdapter;
    private List<Friends> friendsList = new ArrayList<>();
    private RecyclerView mRecylerView;

    private static List<Friends> extractIdsForJSON(JSONObject responseJson) {
        List<Friends> friendsList1 = new ArrayList<>();
        Friends friends = new Friends();
        JSONArray data = responseJson.optJSONArray("data");
        if (data != null) {
            for (int i = 0; i < data.length(); i++) {
                String ids = data.optJSONObject(i).optString("id");
                String names = data.optJSONObject(i).optString("name");
                friends.setName(names);
                friends.setId(ids);
                friendsList1.add(friends);
            }
            return friendsList1;
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fetchfriendsfragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecylerView = (RecyclerView) view.findViewById(R.id.facebookFriends_RecyclerView);
        mRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       fetchFriends();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void fetchFriends() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject responseJSON = response.getJSONObject();
                        if (responseJSON != null) {
                            Log.e(TAG, responseJSON.toString());
                            friendsList = extractIdsForJSON(responseJSON);

                            faceBookFreindsAdapter = new FaceBookFreindsAdapter(getContext(), friendsList);
                            mRecylerView.setAdapter(faceBookFreindsAdapter);
                            faceBookFreindsAdapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "Object is null");

                        }
            /* handle the result */
                    }
                }
        ).executeAsync();
    }
}