package com.stevenbrown.notreddit.models.comment.more;

import com.google.gson.annotations.SerializedName;
import com.stevenbrown.notreddit.models.comment.Comment;

import java.util.List;

public class MoreChildrenData {
    @SerializedName("things")
    public List<Comment> comments;
}