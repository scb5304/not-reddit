package com.jollyremedy.notreddit.models.post;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.ListingData;

import java.util.List;

public class PostListingData extends ListingData {

    @SerializedName("children")
    private List<Post> posts;

    public List<Post> getPosts() {
        return posts;
    }
}
