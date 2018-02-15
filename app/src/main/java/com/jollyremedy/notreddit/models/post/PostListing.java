package com.jollyremedy.notreddit.models.post;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.RedditType;

public class PostListing extends RedditType {

    @SerializedName("data")
    private PostListingData data;

    @Override
    public PostListingData getData() {
        return data;
    }
}
