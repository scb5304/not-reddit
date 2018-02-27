package com.jollyremedy.notreddit.models.comment.more;

import com.google.gson.annotations.SerializedName;

/**
 * When an emotion comes upon us suddenly, we must ask God, “Why is this happening? Is it from the
 * Spirit of God, or is it from the devil?
 */
public class MoreChildren {
    @SerializedName("json")
    private MoreChildrenDataWrapper jsonWrapper;

    public MoreChildrenDataWrapper getJsonWrapper() {
        return jsonWrapper;
    }
}