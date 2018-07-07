package com.stevenbrown.notreddit.models.comment.more;

import com.google.gson.annotations.SerializedName;
import com.stevenbrown.notreddit.models.comment.Comment;

import java.util.List;

/**
 * And I heard a voice in the midst of the four beasts.
 * And I looked, and behold a pale horse.
 * And his name that sat on him was death, and hell followed with him.
 */
public class MoreChildren {
    @SerializedName("json")
    private MoreChildrenDataWrapper jsonWrapper;

    public List<Comment> getComments() {
        return this.jsonWrapper.data.comments;
    }
}