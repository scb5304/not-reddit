package com.jollyremedy.notreddit.ui.postdetail;

import android.util.Pair;

import com.jollyremedy.notreddit.models.comment.Comment;
import com.jollyremedy.notreddit.models.post.Post;

import java.util.ArrayList;
import java.util.List;

public class PostDetailData {
    private List<Comment> comments;
    private Pair<Integer, Integer> commentRangeChanging;
    private Pair<Integer, Integer> commentRangeRemoving;
    private Post post;

    PostDetailData() {
        comments = new ArrayList<>();
        commentRangeChanging = null;
        commentRangeRemoving = null;
        post = null;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Pair<Integer, Integer> getCommentRangeChanging() {
        return commentRangeChanging;
    }

    public void setCommentRangeChanging(Pair<Integer, Integer> commentRangeChanging) {
        this.commentRangeChanging = commentRangeChanging;
    }

    public Pair<Integer, Integer> getCommentRangeRemoving() {
        return commentRangeRemoving;
    }

    public void setCommentRangeRemoving(Pair<Integer, Integer> commentRangeRemoving) {
        this.commentRangeRemoving = commentRangeRemoving;
    }

    public void clearCommentRanges() {
        commentRangeRemoving = null;
        commentRangeChanging = null;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
