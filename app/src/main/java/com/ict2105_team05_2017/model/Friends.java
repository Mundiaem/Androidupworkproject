package com.ict2105_team05_2017.model;

/**
 * Created by Macharia on 2/12/2017.
 */

public class Friends {
    private String name;
    private String id;
    private Boolean isFriend;
    private Boolean acceptedRequest;
    private Boolean sentFriendRequest;

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
}
