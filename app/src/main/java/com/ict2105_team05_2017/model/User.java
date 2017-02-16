package com.ict2105_team05_2017.model;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Macharia on 2/12/2017.
 */
@IgnoreExtraProperties
public class User {
    private String id;
    private String name;
    private String email;
    private String facebookId;
    private List<Friends> friendsList;
    private Friends friends;
    private UserInfo[] providerData;
    private String token;
    private List<Dismissed> dismissedList;
    private String photoUri;


    public User() {
    }

    public User(String id, String name, String email, String facebookId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.facebookId = facebookId;
    }


    public User(Friends friends) {
        this.friends = friends;
    }

    public Friends getFriends() {
        return friends;
    }

    public void setFriends(Friends friends) {
        this.friends = friends;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public List<Friends> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<Friends> friendsList) {
        this.friendsList = friendsList;
    }

    public UserInfo[] getProviderData() {
        return providerData;
    }

    public void setProviderData(UserInfo[] providerData) {
        this.providerData = providerData;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Dismissed> getDismissedList() {
        return dismissedList;
    }

    public void setDismissedList(List<Dismissed> dismissedList) {
        this.dismissedList = dismissedList;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", facebookId='" + facebookId + '\'' +
                ", friendsList=" + friendsList +
                ", friends=" + friends +
                ", providerData=" + Arrays.toString(providerData) +
                '}';
    }
}
