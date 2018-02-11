package com.jollyremedy.notreddit.data;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.ListingResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class PostRepository {

    private static final String TAG = "PostRepository";
    private OAuthRedditApi mRedditApi;

    @Inject
    PostRepository(OAuthRedditApi redditApi) {
        mRedditApi = redditApi;
    }

    public void getNewPosts(SingleObserver<ListingResponse> observer, @Nullable String after) {
        mRedditApi.getNewPosts(after)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }
}
