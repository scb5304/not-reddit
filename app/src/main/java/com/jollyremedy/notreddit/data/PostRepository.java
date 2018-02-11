package com.jollyremedy.notreddit.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.jollyremedy.notreddit.NotRedditExecutors;
import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.ListingResponse;
import com.jollyremedy.notreddit.models.Post;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class PostRepository {

    private static final String TAG = "PostRepository";
    private OAuthRedditApi mRedditApi;

    private MutableLiveData<ListingResponse> mListingResponse;

    @Inject
    PostRepository(OAuthRedditApi redditApi) {
        mRedditApi = redditApi;
        mListingResponse = new MutableLiveData<>();
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
