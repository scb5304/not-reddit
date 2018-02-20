package com.jollyremedy.notreddit.ui.postlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.models.post.PostListingData;
import com.jollyremedy.notreddit.models.post.PostListingSort;
import com.jollyremedy.notreddit.repository.PostRepository;

import java.net.UnknownHostException;

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
            mPostRepository.getPostListing(new ListingResponseFetchObserver(FetchMode.START_FRESH),
                    mSubredditName,
                    PostListingSort.HOT,
                    getCurrentAfter());
        }
        return mListingLiveData;
    }

    LiveData<Boolean> observeResetEndlessScroll() {
        return mEndlessScrollResetLiveData;
    }

    void onLoadMore() {
        mPostRepository.getPostListing(new ListingResponseFetchObserver(FetchMode.ADD_TO_EXISTING_POSTS),
                mSubredditName,
                PostListingSort.HOT,
                getCurrentAfter());
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
        mPostRepository.getPostListing(new ListingResponseFetchObserver(FetchMode.START_FRESH),
                mSubredditName,
                PostListingSort.HOT,
                null);
    }

    public ObservableBoolean isRefreshing() {
        return mDataBindIsRefreshing;
    }

    /// --------------------------------------

    private void onNewPostListingReceived(FetchMode fetchMode, @NonNull PostListing newPostListing) {
        Log.i(TAG, "Got a post listing.");
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
            priorListing.getData().setAfter(newListingData.getAfter());
            mListingLiveData.postValue(priorListing);
            mEndlessScrollResetLiveData.postValue(false);
        }
        mDataBindIsRefreshing.set(false);
    }

    private void onPostListingFetchError(Throwable t) {
        Log.e(TAG, "Failed to get a post listing!", t);
        if (t instanceof UnknownHostException) {
            Log.e(TAG, Log.getStackTraceString(t));
        }
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
            onNewPostListingReceived(mFetchMode, listingResponse);
        }

        @Override
        public void onError(Throwable t) {
            onPostListingFetchError(t);
        }
    }
}
