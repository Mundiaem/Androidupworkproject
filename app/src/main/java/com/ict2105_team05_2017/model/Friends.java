package com.ict2105_team05_2017.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Macharia on 2/12/2017.
 */
@IgnoreExtraProperties
public class Friends {
    private String id;
    private String name;
    private String email;
    private String fbId;
    private Boolean requestFriend;
    private Boolean isFriend;
    private Boolean acceptedRequest;
    private Boolean sentFriendRequest;
    private Boolean isAfriendFromFb;
    private String friendId;
    private Boolean isFriendshipDeclined;

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getFriend() {
        return isFriend;
    }

    public void setFriend(Boolean friend) {
        isFriend = friend;
    }

    public Boolean getAcceptedRequest() {
        return acceptedRequest;
    }

    public void setAcceptedRequest(Boolean acceptedRequest) {
        this.acceptedRequest = acceptedRequest;
    }

    public Boolean getSentFriendRequest() {
        return sentFriendRequest;
    }

    public void setSentFriendRequest(Boolean sentFriendRequest) {
        this.sentFriendRequest = sentFriendRequest;
    }

    public Boolean getRequestFriend() {
        return requestFriend;
    }

    public void setRequestFriend(Boolean requestFriend) {
        this.requestFriend = requestFriend;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAfriendFromFb() {
        return isAfriendFromFb;
    }

    public void setAfriendFromFb(Boolean afriendFromFb) {
        isAfriendFromFb = afriendFromFb;
    }

    public Boolean getFriendshipDeclined() {
        return isFriendshipDeclined;
    }

    public void setFriendshipDeclined(Boolean friendshipDeclined) {
        isFriendshipDeclined = friendshipDeclined;
    }
}
