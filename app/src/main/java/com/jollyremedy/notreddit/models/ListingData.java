package com.jollyremedy.notreddit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListingData {

    @SerializedName("after")
    private String after;

    @SerializedName("dist")
    private int dist;

    @SerializedName("mod_hash")
    private String modHash;

    @SerializedName("whitelist_status")
    private String whitelistStatus;

    @SerializedName("children")
    private List<Post> posts;

    public String getAfter() {
        return after;
    }

    public int getDist() {
        return dist;
    }

    public String getModHash() {
        return modHash;
    }

    public String getWhitelistStatus() {
        return whitelistStatus;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
