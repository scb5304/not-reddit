package com.jollyremedy.notreddit.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.ListingResponse;
import com.jollyremedy.notreddit.models.Post;
import com.jollyremedy.notreddit.util.Utility;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class PostRepository {

    private static final String TAG = "PostRepository";
    private OAuthRedditApi mRedditApi;
    private Gson mGson;
    private MutableLiveData<ListingResponse> mListingResponse;

    @Inject
    PostRepository(OAuthRedditApi redditApi, Gson gson) {
        mRedditApi = redditApi;
        mGson = gson;
        mListingResponse = new MutableLiveData<>();
    }

    public MutableLiveData<ListingResponse> getDummyListingResponse(Context context) {
        String initialJson = Utility.readStringFromJson(context, "demo_posts_initial");
        ListingResponse listingResponse = mGson.fromJson(initialJson, ListingResponse.class);
        Completable.fromAction(() -> {
            mListingResponse.postValue(listingResponse);
        }).subscribeOn(Schedulers.newThread())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        return mListingResponse;
    }

    public MutableLiveData<ListingResponse> getDummyListingResponseAgain(Context context) {
        String initialJson = Utility.readStringFromJson(context, "demo_posts_after");
        ListingResponse listingResponse = mGson.fromJson(initialJson, ListingResponse.class);
        Completable.fromAction(() -> {
            mListingResponse.postValue(listingResponse);
        }).subscribeOn(Schedulers.newThread())
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        return mListingResponse;
    }

    public MutableLiveData<ListingResponse> getListingResponse() {
        mRedditApi.getNewPosts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new SingleObserver<ListingResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "Getting posts...");
                    }

                    @Override
                    public void onSuccess(ListingResponse listingResponse) {
                        mListingResponse.postValue(listingResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Failed to get a Reddit listing.", e);
                    }
                });
        return mListingResponse;
    }
}
