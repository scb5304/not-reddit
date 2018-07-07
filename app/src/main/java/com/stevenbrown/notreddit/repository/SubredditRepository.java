package com.stevenbrown.notreddit.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;
import com.stevenbrown.notreddit.api.OAuthRedditApi;
import com.stevenbrown.notreddit.models.subreddit.SubredditForUserWhere;
import com.stevenbrown.notreddit.models.subreddit.SubredditListing;
import com.stevenbrown.notreddit.models.subreddit.SubredditWhere;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SubredditRepository {

    private OAuthRedditApi mRedditApi;

    @Inject
    SubredditRepository(OAuthRedditApi redditApi) {
        mRedditApi = redditApi;
    }

    public Single<SubredditListing> getSubredditsWhere(@SubredditWhere String where) {
        return mRedditApi.getSubredditListingWhere(where)
                .onErrorResumeNext(this::onErrorSubredditListing)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<SubredditListing> getSubredditsForUserWhere(@SubredditForUserWhere String where) {
        return mRedditApi.getSubredditListingForUserWhere(where)
                .onErrorResumeNext(this::onErrorSubredditListing)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<SubredditListing> onErrorSubredditListing(Throwable t) {
        Timber.e(t);
        return Single.just(new SubredditListing());
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
        if (listingObjects.length > 1) {
            for (Object obj : listingObjects) {
                SubredditListing listing = (SubredditListing) obj;
                firstListing.addListing(listing);
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
                Timber.e("Invalid SubredditWhere: %s", subredditWhere);
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
                Timber.e("Invalid SubredditForUserWhere: %s", subredditForUserWhere);
                it.remove();
            }
        }
        return subredditForUserWheres;
    }
}
