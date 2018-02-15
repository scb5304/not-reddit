package com.jollyremedy.notreddit.models.parent;

import com.google.gson.annotations.SerializedName;

/*
 * A common wrapper that contains a "kind" and "data" field.
 */
public abstract class RedditType {
    @SerializedName("kind")
    protected static RedditTypePrefix kind;

    @SuppressWarnings("unused")
    public enum RedditTypePrefix {
        t1, //Comment
        t2, //Account
        t3, //Link
        t4, //Message
        t5, //Subreddit
        t6  //Award
    }

    public abstract Object getData();
}
