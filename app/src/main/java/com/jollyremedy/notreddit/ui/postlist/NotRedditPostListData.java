package com.jollyremedy.notreddit.ui.postlist;


import com.google.common.collect.Range;
import com.jollyremedy.notreddit.models.post.PostListing;

public class NotRedditPostListData {
    private PostListing postListing;
    private Range<Integer> postsChangingRange;
    private Range<Integer> postsDeletingRange;

    public void setPostListing(PostListing postListing) {
        this.postListing = postListing;
    }

    public PostListing getPostListing() {
        return this.postListing;
    }

    public void setPostsChangingRange(Range<Integer> range) {
        this.postsChangingRange = range;
    }

    public Range getPostsChangingRange() {
        return this.postsChangingRange;
    }

    public void setPostsDeletingRange(Range<Integer> postsDeletingRange) {
        this.postsDeletingRange = postsDeletingRange;
    }

    public Range<Integer> getPostsDeletingRange() {
        return postsDeletingRange;
    }

    public void clearRanges() {
        this.postsChangingRange = null;
        this.postsDeletingRange = null;
    }
}
