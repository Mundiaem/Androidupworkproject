package com.ict2105_team05_2017.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Macharia on 2/16/2017.
 */
@IgnoreExtraProperties
public class Dismissed {
    private String name;
    private String id;
    private Boolean cleared;

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

    public Boolean getCleared() {
        return cleared;
    }

    public void setCleared(Boolean cleared) {
        this.cleared = cleared;
    }
}
