package com.jollyremedy.notreddit.models.comment;

import com.google.gson.annotations.JsonAdapter;
import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.api.adapter.SensicalCommentsAdapter;

@JsonAdapter(SensicalCommentsAdapter.class)
public class PostWithCommentListing {
    private PostListing postListing;
    private CommentListing commentListing;

    public PostWithCommentListing(PostListing postListing, CommentListing commentListing) {
        this.postListing = postListing;
        this.commentListing = commentListing;
    }

    public PostListing getPostListing() {
        return postListing;
    }

    public void setPostListing(PostListing postListing) {
        this.postListing = postListing;
    }

    public CommentListing getCommentListing() {
        return commentListing;
    }

    public void setCommentListing(CommentListing commentListing) {
        this.commentListing = commentListing;
    }


}
