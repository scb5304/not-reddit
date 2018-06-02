package com.jollyremedy.notreddit.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;
import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.models.post.PostListingSort;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class PostRepository {

    private OAuthRedditApi mRedditApi;

    @Inject
    PostRepository(OAuthRedditApi redditApi) {
        mRedditApi = redditApi;
    }

    public Single<PostListing> getPostListing(@NonNull String subredditName,
                                              @NonNull @PostListingSort String sort,
                                              @Nullable String after) {
        String prefixedSubredditName = Strings.isNullOrEmpty(subredditName) ? "": "/r/" + subredditName;
        return mRedditApi.getPostsBySubreddit(prefixedSubredditName, sort, after)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
