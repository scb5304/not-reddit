package com.jollyremedy.notreddit.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.base.Strings;
import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.subreddit.Subreddit;
import com.jollyremedy.notreddit.models.subreddit.SubredditForUserWhere;
import com.jollyremedy.notreddit.models.subreddit.SubredditListing;
import com.jollyremedy.notreddit.models.subreddit.SubredditWhere;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public Single<SubredditListing> getSubredditsForParams(@Nullable List<String> subredditWheres,
                                                           @Nullable List<String> subredditForUserWheres) {
        subredditWheres = cleanseSubredditWheres(subredditWheres);
        subredditForUserWheres = cleanseSubredditForUserWheres(subredditForUserWheres);

        List<Single<SubredditListing>> singles = new ArrayList<>();
        for (String subredditWhere : subredditWheres) {
            singles.add(getSubredditsWhere(subredditWhere));
        }
        for (String subredditForUserWhere: subredditForUserWheres) {
            singles.add(getSubredditsForUserWhere(subredditForUserWhere));
        }

        return Single.zip(singles, this::concatSubredditListingObjects);
    }

    private SubredditListing concatSubredditListingObjects(Object[] listingObjects) throws IllegalArgumentException {
        if (listingObjects == null || listingObjects.length == 0) {
            throw new IllegalArgumentException("Must have at least one SubredditListing result to combine them.");
        }
        SubredditListing firstListing = (SubredditListing) listingObjects[0];
        List<Subreddit> subreddits = firstListing.getSubreddits();

        if (listingObjects.length > 1) {
            for (Object obj : listingObjects) {
                SubredditListing listing = (SubredditListing) obj;
                subreddits.addAll(listing.getSubreddits());
            }
        }

        return firstListing;
    }

    @NonNull
    private List<String> cleanseSubredditWheres(@Nullable List<String> subredditWheres) {
        if (subredditWheres == null) {
            return new ArrayList<>();
        }

        Iterator<String> it = subredditWheres.iterator();
        while (it.hasNext()) {
            String subredditWhere = it.next();
            if (!SubredditWhere.LIST.contains(subredditWhere)) {
                Log.e(TAG, "Invalid SubredditWhere: " + subredditWhere);
                it.remove();
            }
        }
        return subredditWheres;
    }

    @NonNull
    private List<String> cleanseSubredditForUserWheres(@Nullable List<String> subredditForUserWheres) {
        if (subredditForUserWheres == null) {
            return new ArrayList<>();
        }

        Iterator<String> it = subredditForUserWheres.iterator();
        while (it.hasNext()) {
            String subredditForUserWhere = it.next();
            if (Strings.isNullOrEmpty(subredditForUserWhere) || !SubredditForUserWhere.LIST.contains(subredditForUserWhere)) {
                Log.e(TAG, "Invalid SubredditForUserWhere: " + subredditForUserWhere);
                it.remove();
            }
        }
        return subredditForUserWheres;
    }
}
