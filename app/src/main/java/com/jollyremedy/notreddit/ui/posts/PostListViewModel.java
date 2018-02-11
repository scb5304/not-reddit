package com.jollyremedy.notreddit.ui.posts;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.google.gson.Gson;
import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.data.PostRepository;
import com.jollyremedy.notreddit.models.ListingResponse;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PostListViewModel extends AndroidViewModel{

    private static final String TAG = "PostListViewModel";
    private final MediatorLiveData<ListingResponse> mObservableListingResponse;
    private OAuthRedditApi mRedditApi;
    private PostRepository mPostRepository;
    MutableLiveData<ListingResponse> listingResponse;

    @Inject
    PostListViewModel(Application application,
                      OAuthRedditApi redditApi,
                      PostRepository postRepository) {
        super(application);
        mRedditApi = redditApi;
        mPostRepository = postRepository;

        mObservableListingResponse = new MediatorLiveData<>();
        mObservableListingResponse.setValue(null);

        listingResponse = mPostRepository.getListingResponse();
        mObservableListingResponse.addSource(listingResponse, new Observer<ListingResponse>() {
            @Override
            public void onChanged(@Nullable ListingResponse value) {
                mObservableListingResponse.setValue(value);
            }
        });
    }

    LiveData<ListingResponse> getListingResponse() {
        return mObservableListingResponse;
    }

    void onSwipeToRefresh() {
        mPostRepository.getListingResponse();
    }
}
