package com.jollyremedy.notreddit.ui.postlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;

import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.models.post.PostListingData;
import com.jollyremedy.notreddit.repository.PostRepository;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class PostListViewModel extends ViewModel {

    private static final String TAG = "PostListViewModel";
    private PostRepository mPostRepository;
    private MutableLiveData<PostListing> mListingLiveData;
    private MutableLiveData<Boolean> mEndlessScrollResetLiveData;
    private ObservableBoolean mDataBindIsRefreshing;
    private String mSubredditName = "all";

    public enum FetchMode {
        START_FRESH,
        ADD_TO_EXISTING_POSTS
    }

    @Inject
    PostListViewModel(PostRepository postRepository) {
        mPostRepository = postRepository;

        mListingLiveData = new MutableLiveData<>();
        mEndlessScrollResetLiveData = new MutableLiveData<>();
        mDataBindIsRefreshing = new ObservableBoolean();
    }

    LiveData<PostListing> getObservableListing(String subredditName) {
        mSubredditName = subredditName;
        if (mListingLiveData.getValue() == null) {
            mPostRepository.getHotPosts(new ListingResponseFetchObserver(FetchMode.START_FRESH), mSubredditName, getCurrentAfter());
        }
        return mListingLiveData;
    }

    LiveData<Boolean> observeResetEndlessScroll() {
        return mEndlessScrollResetLiveData;
    }

    void onLoadMore() {
        mPostRepository.getHotPosts(new ListingResponseFetchObserver(FetchMode.ADD_TO_EXISTING_POSTS), mSubredditName, getCurrentAfter());
    }

    @Nullable
    private String getCurrentAfter() {
        if (mListingLiveData.getValue() != null && mListingLiveData.getValue().getData() != null) {
            return mListingLiveData.getValue().getData().getAfter();
        } else {
            return null;
        }
    }

    // --------------------------------------
    // Binding
    // --------------------------------------

    public void onRefresh() {
        mPostRepository.getHotPosts(new ListingResponseFetchObserver(FetchMode.START_FRESH), mSubredditName, null);
    }

    public ObservableBoolean isRefreshing() {
        return mDataBindIsRefreshing;
    }

    /// --------------------------------------

    private void onNewPostListingReceived(FetchMode fetchMode, @NonNull PostListing newPostListing) {
        PostListing priorListing = mListingLiveData.getValue();
        if (priorListing == null || fetchMode == FetchMode.START_FRESH) {
            //We didn't have data, or we're starting over; post the fetched ListingResponse.
            mListingLiveData.postValue(newPostListing);
            mEndlessScrollResetLiveData.postValue(true);
        } else {
            //Appropriately update the existing PostListing.
            PostListingData priorListingData = priorListing.getData();
            PostListingData newListingData = newPostListing.getData();

            if (priorListingData.hasPosts()) {
                priorListingData.addAllPosts(newListingData.getPosts());
            } else {
                priorListingData.setPosts(newListingData.getPosts());
            }
            mListingLiveData.postValue(priorListing);
            mEndlessScrollResetLiveData.postValue(false);
        }
        mDataBindIsRefreshing.set(false);
    }

    private void onPostListingFetchError() {
        mDataBindIsRefreshing.set(false);
    }

    protected class ListingResponseFetchObserver implements SingleObserver<PostListing> {
        @VisibleForTesting
        FetchMode mFetchMode;

        ListingResponseFetchObserver(FetchMode fetchMode) {
            mFetchMode = fetchMode;
        }

        @Override
        public void onSubscribe(Disposable d) {
            mDataBindIsRefreshing.set(true);
            Log.i(TAG, "Getting post listing...");
        }

        @Override
        public void onSuccess(PostListing listingResponse) {
            Log.i(TAG, "Got a post listing.");
            onNewPostListingReceived(mFetchMode, listingResponse);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Failed to get a post listing!", e);
            onPostListingFetchError();
        }
    }
}
