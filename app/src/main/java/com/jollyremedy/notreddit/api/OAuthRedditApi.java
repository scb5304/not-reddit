package com.jollyremedy.notreddit.api;

import com.jollyremedy.notreddit.models.comment.PostWithCommentListing;
import com.jollyremedy.notreddit.models.post.PostListing;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OAuthRedditApi {
    @GET("/r/{subredditName}")
    Single<PostListing> getPostsBySubreddit(@Path("subredditName") String subredditName,
                                            @Query("after") String after);

    @GET("/comments/{postId}?threaded&raw_json=1")
    Single<PostWithCommentListing> getCommentListing(@Path("postId") String postId);
}
