package com.jollyremedy.notreddit.api;

import com.jollyremedy.notreddit.models.ListingResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OAuthRedditApi {
    @GET("/r/{subredditName}")
    Single<ListingResponse> getPostsBySubreddit(@Path("subredditName") String subredditName,
                                                @Query("after") String after);
}
