package com.jollyremedy.notreddit.models.post;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.RedditType;

public class PostData {

    @SerializedName("title")
    private String title;

    @SerializedName("domain")
    private String domain;

    @SerializedName("subreddit")
    private String subreddit;

    @SerializedName("num_comments")
    private Integer commentCount;

    public String getTitle() {
        return title;
    }

    public String getDomain() {
        return domain;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public Integer getCommentCount() {
        return commentCount;
    }
}
