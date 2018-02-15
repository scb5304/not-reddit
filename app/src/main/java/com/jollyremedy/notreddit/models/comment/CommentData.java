package com.jollyremedy.notreddit.models.comment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentData {
    @SerializedName("body")
    private String body;

    @SerializedName("replies")
    private List<CommentListing> replies;

    public String getBody() {
        return body;
    }
}
