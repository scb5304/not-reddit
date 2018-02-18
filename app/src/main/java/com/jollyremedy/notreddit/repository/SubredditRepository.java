package com.jollyremedy.notreddit.repository;

import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.subreddit.SubredditListing;
import com.jollyremedy.notreddit.models.subreddit.SubredditWhere;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class SubredditRepository {

    private static final String TAG = "SubredditRepository";
    private OAuthRedditApi mRedditApi;

    @Inject
    SubredditRepository(OAuthRedditApi redditApi) {
        mRedditApi = redditApi;
    }

    public void getSubredditsWhere(@SubredditWhere String where, SingleObserver<SubredditListing> observer) {
        mRedditApi.getSubredditListing(where)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }
}
