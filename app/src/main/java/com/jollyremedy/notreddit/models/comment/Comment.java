package com.jollyremedy.notreddit.models.comment;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.RedditType;

public class Comment extends RedditType {
    @SerializedName("data")
    private CommentData data;

    @Override
    public CommentData getData() {
        return data;
    }
}
