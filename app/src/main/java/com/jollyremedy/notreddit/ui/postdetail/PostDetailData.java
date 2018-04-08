package com.jollyremedy.notreddit.ui.postdetail;

import com.jollyremedy.notreddit.models.comment.Comment;
import com.jollyremedy.notreddit.models.post.Post;

import java.util.ArrayList;
import java.util.List;

public class PostDetailData {
    private List<Comment> comments;
    private Post post;

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
}
