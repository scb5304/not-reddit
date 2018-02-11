package com.jollyremedy.notreddit.api;

import com.jollyremedy.notreddit.models.ListingResponse;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface OAuthRedditApi {
    @GET("/new")
    Single<ListingResponse> getNewPosts();
}
