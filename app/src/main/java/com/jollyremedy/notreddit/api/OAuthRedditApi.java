package com.jollyremedy.notreddit.api;

import com.jollyremedy.notreddit.models.comment.CommentListing;
import com.jollyremedy.notreddit.models.post.PostListing;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OAuthRedditApi {
    @GET("/r/{subredditName}")
    Single<PostListing> getPostsBySubreddit(@Path("subredditName") String subredditName,
                                            @Query("after") String after);

    /**
     * This is a List only because the first object returned by Reddit is actually the Post itself.
     * So...ignore that one.
     */
    @GET("r/{subredditName}/comments/{postId}?threaded")
    Single<List<CommentListing>> getCommentListing(@Path("subredditName") String subredditName,
                                                   @Path("postId") String postId);
}
