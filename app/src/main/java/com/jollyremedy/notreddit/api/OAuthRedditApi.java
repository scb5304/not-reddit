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

    @GET("r/leagueoflegends/comments/7xd37j?threaded")
    Single<List<CommentListing>> getTestCommentListings();
}
