package com.jollyremedy.notreddit.ui.postlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.repository.PostRepository;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class PostListViewModel extends ViewModel {

    private static final String TAG = "PostListViewModel";
    private PostRepository mPostRepository;
    private MutableLiveData<PostListing> mListingLiveData;
    private String mLatestAfter;
    private String mSubredditName = "all";

    @Inject
    PostListViewModel(PostRepository postRepository) {
        mPostRepository = postRepository;
        mListingLiveData = new MutableLiveData<>();
    }

    LiveData<PostListing> getObservableListing(String subredditName) {
        mSubredditName = subredditName;
        mPostRepository.getHotPosts(new ListingResponseFetchObserver(FetchMode.START_FRESH), mSubredditName, mLatestAfter);
        return mListingLiveData;
    }

    void onSwipeToRefresh() {
        mLatestAfter = null;
        mPostRepository.getHotPosts(new ListingResponseFetchObserver(FetchMode.START_FRESH), mSubredditName, mLatestAfter);
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
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Failed to get a listing response.", e);
        }
    }
}
