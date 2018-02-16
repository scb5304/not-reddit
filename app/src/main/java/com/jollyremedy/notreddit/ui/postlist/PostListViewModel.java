package com.jollyremedy.notreddit.ui.postlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.repository.PostRepository;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class PostListViewModel extends ViewModel {

    @SuppressWarnings("WeakerAccess")
    public ObservableBoolean isRefreshing;

    private static final String TAG = "PostListViewModel";
    private PostRepository mPostRepository;
    private MutableLiveData<PostListing> mListingLiveData;
    private String mLatestAfter;
    private String mSubredditName = "all";

    @Inject
    PostListViewModel(PostRepository postRepository) {
        mPostRepository = postRepository;
        mListingLiveData = new MutableLiveData<>();
        isRefreshing = new ObservableBoolean();
    }

    LiveData<PostListing> getObservableListing(String subredditName) {
        mSubredditName = subredditName;
        mPostRepository.getHotPosts(new ListingResponseFetchObserver(FetchMode.START_FRESH), mSubredditName, mLatestAfter);
        return mListingLiveData;
    }

    void onLoadMore() {
        mPostRepository.getHotPosts(new ListingResponseFetchObserver(FetchMode.ADD_TO_EXISTING_POSTS), mSubredditName, mLatestAfter);
    }

    public enum FetchMode {
        START_FRESH,
        ADD_TO_EXISTING_POSTS
    }

    protected class ListingResponseFetchObserver implements SingleObserver<PostListing> {
        @VisibleForTesting
        FetchMode mFetchMode;

        ListingResponseFetchObserver(FetchMode fetchMode) {
            mFetchMode = fetchMode;
        }

        @Override
        public void onSubscribe(Disposable d) {}

        @Override
        public void onSuccess(PostListing listingResponse) {
            mListingLiveData.postValue(listingResponse);
            isRefreshing.set(false);
        }

        @Override
        public void onError(Throwable e) {
            isRefreshing.set(false);
            Log.e(TAG, "Failed to get a listing response.", e);
        }
    }

    // --------------------------------------
    // Binding
    // --------------------------------------

    @SuppressWarnings("unused")
    public void onRefresh() {
        mLatestAfter = null;
        mPostRepository.getHotPosts(new ListingResponseFetchObserver(FetchMode.START_FRESH), mSubredditName, mLatestAfter);
        isRefreshing.set(true);
    }
}
