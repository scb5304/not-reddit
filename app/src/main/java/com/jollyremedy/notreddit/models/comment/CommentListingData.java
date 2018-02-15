package com.jollyremedy.notreddit.models.comment;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.ListingData;

import java.util.List;

public final class CommentListingData extends ListingData {
    @SerializedName("children")
    private List<Comment> comments;
}
