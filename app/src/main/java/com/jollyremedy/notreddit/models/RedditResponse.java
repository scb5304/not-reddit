package com.jollyremedy.notreddit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RedditResponse {

    @Expose
    @SerializedName("kind")
    protected String kind;
}
