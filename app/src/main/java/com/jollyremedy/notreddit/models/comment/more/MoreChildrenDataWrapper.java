package com.jollyremedy.notreddit.models.comment.more;

import com.google.gson.annotations.SerializedName;

public class MoreChildrenDataWrapper {
    @SerializedName("data")
    private MoreChildrenData data;

    public MoreChildrenData getData() {
        return data;
    }
}