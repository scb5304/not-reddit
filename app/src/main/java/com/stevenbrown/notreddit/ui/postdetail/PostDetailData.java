package com.stevenbrown.notreddit.ui.postdetail;

import com.google.common.collect.Range;
import com.stevenbrown.notreddit.models.comment.Comment;
import com.stevenbrown.notreddit.models.post.Post;

import java.util.ArrayList;
import java.util.List;

public class PostDetailData {
    private List<Comment> comments;
    private Post post;
    private Range<Integer> commentsChangingRange;
    private Range<Integer> commentsDeletingRange;

    PostDetailData() {
        comments = new ArrayList<>();
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Range<Integer> getCommentsChangingRange() {
        return commentsChangingRange;
    }

    public void setCommentsChangingRange(Range<Integer> commentsChangingRange) {
        this.commentsChangingRange = commentsChangingRange;
    }

    public Range<Integer> getCommentsDeletingRange() {
        return commentsDeletingRange;
    }

    public void setCommentsDeletingRange(Range<Integer> commentsDeletingRange) {
        this.commentsDeletingRange = commentsDeletingRange;
    }

    public void clearRanges() {
        this.commentsChangingRange = null;
        this.commentsDeletingRange = null;
    }
}
