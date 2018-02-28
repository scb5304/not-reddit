package com.jollyremedy.notreddit.models.comment.more;

import com.google.gson.annotations.SerializedName;

/**
 * And I heard a voice in the midst of the four beasts.
 * And I looked, and behold a pale horse.
 * And his name that sat on him was death, and hell followed with him.
 */
public class MoreChildren {
    @SerializedName("json")
    private MoreChildrenDataWrapper jsonWrapper;

    public MoreChildrenDataWrapper getJsonWrapper() {
        return jsonWrapper;
    }
}