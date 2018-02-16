package com.jollyremedy.notreddit.models.comment;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.util.EmptyStringAsNullTypeAdapter;

import java.util.List;

public class CommentData {
    @SerializedName("body")
    private String body;

    @JsonAdapter(EmptyStringAsNullTypeAdapter.class)
    @SerializedName("replies")
    private CommentListing replies;

    public String getBody() {
        return body;
    }
}
