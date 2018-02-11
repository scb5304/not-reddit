package com.jollyremedy.notreddit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListingData {

    @Expose
    @SerializedName("after")
    private String after;

    @Expose
    @SerializedName("dist")
    private int dist;

    @Expose
    @SerializedName("mod_hash")
    private String modHash;

    @Expose
    @SerializedName("whitelist_status")
    private String whitelistStatus;

    @Expose
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
