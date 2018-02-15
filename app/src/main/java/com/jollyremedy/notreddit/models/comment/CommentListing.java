package com.jollyremedy.notreddit.models.comment;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.RedditType;
import com.jollyremedy.notreddit.models.post.PostListingData;

public class CommentListing extends RedditType {

    @SerializedName("data")
    private CommentListingData data;

    @Override
    public CommentListingData getData() {
        return data;
    }
}
