package com.jollyremedy.notreddit.api;

import com.jollyremedy.notreddit.models.comment.PostWithCommentListing;
import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.models.subreddit.SubredditListing;
import com.jollyremedy.notreddit.models.subreddit.SubredditWhere;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OAuthRedditApi {
    @GET("/r/{subredditName}?&raw_json=1")
    Single<PostListing> getPostsBySubreddit(@Path("subredditName") String subredditName,
                                            @Query("after") String after);

    @GET("/comments/{postId}?threaded&raw_json=1")
    Single<PostWithCommentListing> getCommentListing(@Path("postId") String postId);

    @GET("/subreddits/{where}")
    Single<SubredditListing> getSubredditListing(@Path("where") @SubredditWhere String where);
}
