package com.jollyremedy.notreddit.repository;

import android.support.annotation.Nullable;

import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.post.PostListing;

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

    public void getHotPosts(SingleObserver<PostListing> observer, String subredditName, @Nullable String after) {
        mRedditApi.getPostsBySubreddit(subredditName, after)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }
}
