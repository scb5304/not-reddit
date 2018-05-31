package com.jollyremedy.notreddit.repository;

import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.subreddit.SubredditForUserWhere;
import com.jollyremedy.notreddit.models.subreddit.SubredditListing;
import com.jollyremedy.notreddit.models.subreddit.SubredditWhere;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
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

    public Single<SubredditListing> getSubredditsWhere(@SubredditWhere String where) {
        return mRedditApi.getSubredditListingWhere(where)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<SubredditListing> getSubredditsForUserWhere(@SubredditForUserWhere String where) {
        return mRedditApi.getSubredditListingForUserWhere(where)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
