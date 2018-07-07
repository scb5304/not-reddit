package com.stevenbrown.notreddit.ui.postlist;


import com.google.common.collect.Range;
import com.stevenbrown.notreddit.models.post.PostListing;

public class NotRedditPostListData {
    private String currentSubreddit;
    private PostListing postListing;
    private Range<Integer> postsChangingRange;
    private Range<Integer> postsDeletingRange;

    public NotRedditPostListData setCurrentSubredditName(String currentSubreddit) {
        this.currentSubreddit = currentSubreddit;
        return this;
    }

    public String getCurrentSubreddit() {
        return currentSubreddit;
    }

    public NotRedditPostListData setPostListing(PostListing postListing) {
        this.postListing = postListing;
        return this;
    }

    public PostListing getPostListing() {
        return this.postListing;
    }

    public NotRedditPostListData setPostsChangingRange(Range<Integer> range) {
        this.postsChangingRange = range;
        return this;
    }

    public Range<Integer> getPostsChangingRange() {
        return this.postsChangingRange;
    }

    public NotRedditPostListData setPostsDeletingRange(Range<Integer> postsDeletingRange) {
        this.postsDeletingRange = postsDeletingRange;
        return this;
    }

    public Range<Integer> getPostsDeletingRange() {
        return postsDeletingRange;
    }

    public NotRedditPostListData clearRanges() {
        this.postsChangingRange = null;
        this.postsDeletingRange = null;
        return this;
    }
}
