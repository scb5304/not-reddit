package com.jollyremedy.notreddit.models.auth;

import com.google.gson.annotations.SerializedName;

public class RedditAccount {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("comment_karma")
    private String commentKarma;

    @SerializedName("link_karma")
    private String linkKarma;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCommentKarma() {
        return commentKarma;
    }

    public String getLinkKarma() {
        return linkKarma;
    }
}
