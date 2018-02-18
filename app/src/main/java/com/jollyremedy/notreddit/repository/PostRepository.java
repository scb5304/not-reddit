package com.jollyremedy.notreddit.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;
import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.models.post.PostListingSort;

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

    public void getPostListing(SingleObserver<PostListing> observer, String subredditName,
                               @NonNull @PostListingSort String sort,
                               @Nullable String after) {
        String prefixedSubredditName = Strings.isNullOrEmpty(subredditName) ? "": "/r/" + subredditName;
        mRedditApi.getPostsBySubreddit(prefixedSubredditName, sort, after)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }
}
