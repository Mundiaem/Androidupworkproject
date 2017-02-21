package com.ict2105_team05_2017.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Macharia on 2/12/2017.
 */
@IgnoreExtraProperties
public class User implements Parcelable {
    private String id;
    private String name;
    private String email;
    private String facebookId;
    private ArrayList<Friends> friendsList;
    private Friends friends;
    private UserInfo[] providerData;
    private String token;
    private ArrayList<Dismissed> dismissedList;
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

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
        facebookId = in.readString();
        token = in.readString();
        photoUri = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public ArrayList<Friends> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(ArrayList<Friends> friendsList) {
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

    public ArrayList<Dismissed> getDismissedList() {
        return dismissedList;
    }

    public void setDismissedList(ArrayList<Dismissed> dismissedList) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(facebookId);
        parcel.writeString(token);
        parcel.writeString(photoUri);
        parcel.writeList(friendsList);
        parcel.writeList(dismissedList);
    }

}
