package com.jollyremedy.notreddit.ui.postlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.jollyremedy.notreddit.models.ListingResponse;
import com.jollyremedy.notreddit.models.Post;
import com.jollyremedy.notreddit.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class PostListViewModel extends ViewModel {

    private static final String TAG = "PostListViewModel";
    private PostRepository mPostRepository;
    private MutableLiveData<List<Post>> mPostsLiveData;
    private String mLatestAfter;
    private String mSubredditName = "all";

    @Inject
    PostListViewModel(PostRepository postRepository) {
        mPostRepository = postRepository;
        mPostsLiveData = new MutableLiveData<>();
    }

    LiveData<List<Post>> getObservablePosts(String subredditName) {
        mSubredditName = subredditName;
        mPostRepository.getHotPosts(new ListingResponseFetchObserver(FetchMode.START_FRESH), mSubredditName, mLatestAfter);
        return mPostsLiveData;
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

    protected class ListingResponseFetchObserver implements SingleObserver<ListingResponse> {
        @VisibleForTesting
        FetchMode mFetchMode;

        ListingResponseFetchObserver(FetchMode fetchMode) {
            mFetchMode = fetchMode;
        }

        @Override
        public void onSubscribe(Disposable d) {
            Log.i(TAG, "Getting a listing response...");
        }

        @Override
        public void onSuccess(ListingResponse listingResponse) {
            List<Post> postsFetched = listingResponse.getListingData().getPosts();
            if (mFetchMode == FetchMode.START_FRESH) {
                mPostsLiveData.postValue(postsFetched);
            } else {
                List<Post> postsWeAlreadyHave = mPostsLiveData.getValue();
                List<Post> allPosts = new ArrayList<>();
                if (postsWeAlreadyHave != null) {
                    allPosts.addAll(postsWeAlreadyHave);
                }
                allPosts.addAll(postsFetched);
                mPostsLiveData.postValue(allPosts);
            }
            mLatestAfter = listingResponse.getListingData().getAfter();
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Failed to get a listing response.", e);
        }
    }
}
