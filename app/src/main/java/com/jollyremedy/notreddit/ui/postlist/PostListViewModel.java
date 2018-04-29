package com.jollyremedy.notreddit.ui.postlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.common.collect.Range;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.models.post.PostListingData;
import com.jollyremedy.notreddit.models.post.PostListingSort;
import com.jollyremedy.notreddit.repository.PostRepository;
import com.jollyremedy.notreddit.ui.common.NavigationController;

import java.net.UnknownHostException;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class PostListViewModel extends ViewModel {

    private static final String TAG = "PostListViewModel";
    private PostRepository mPostRepository;
    private NavigationController mNavigationController;
    private MutableLiveData<NotRedditPostListData> mPostListLiveData;
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

        mPostListLiveData = new MutableLiveData<>();
        mEndlessScrollResetLiveData = new MutableLiveData<>();
        mDataBindIsRefreshing = new ObservableBoolean();
    }

    public void setNavigationController(NavigationController navigationController) {
        mNavigationController = navigationController;
    }

    LiveData<NotRedditPostListData> getObservableListing(String subredditName) {
        mSubredditName = subredditName;
        if (mPostListLiveData.getValue() == null) {
            mPostRepository.getPostListing(new ListingResponseFetchObserver(FetchMode.START_FRESH),
                    mSubredditName,
                    PostListingSort.HOT,
                    getCurrentAfter());
        }
        return mPostListLiveData;
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
        if (mPostListLiveData.getValue() != null && mPostListLiveData.getValue().getPostListing() != null) {
            return mPostListLiveData.getValue().getPostListing().getData().getAfter();
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

    public void onPostClicked(Post post) {
        mNavigationController.navigateToPostGeneric(post);
    }

    public void onPostCommentsClicked(Post post) {
        mNavigationController.navigateToPostDetail(post);
    }

    /// --------------------------------------

    private void onNewPostListingReceived(FetchMode fetchMode, @NonNull PostListing newPostListing) {
        Log.i(TAG, "Got a post listing.");
        NotRedditPostListData priorListing = mPostListLiveData.getValue();
        if (priorListing == null || fetchMode == FetchMode.START_FRESH) {
            //We didn't have data, or we're starting over; post the fetched ListingResponse.
            priorListing = new NotRedditPostListData();
            priorListing.setPostListing(newPostListing);
            priorListing.setPostsChangingRange(Range.closed(0, newPostListing.getData().getPosts().size()));
            mPostListLiveData.postValue(priorListing);
            mEndlessScrollResetLiveData.postValue(true);
        } else {
            //Appropriately update the existing PostListing.
            PostListingData priorListingData = priorListing.getPostListing().getData();
            PostListingData newListingData = newPostListing.getData();

            int priorListSize = priorListingData.getPosts().size();
            priorListing.setPostsChangingRange(Range.closed(priorListSize, newPostListing.getData().getPosts().size() + priorListSize));

            if (priorListingData.hasPosts()) {
                priorListingData.addAllPosts(newListingData.getPosts());
            } else {
                priorListingData.setPosts(newListingData.getPosts());
            }
            priorListing.getPostListing().getData().setAfter(newListingData.getAfter());

            mPostListLiveData.postValue(priorListing);
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
