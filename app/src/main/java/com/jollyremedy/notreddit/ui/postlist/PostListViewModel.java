package com.jollyremedy.notreddit.ui.postlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.Range;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.models.post.PostListingSort;
import com.jollyremedy.notreddit.repository.PostRepository;
import com.jollyremedy.notreddit.ui.common.NavigationController;

import javax.inject.Inject;

import timber.log.Timber;

public class PostListViewModel extends ViewModel {

    private PostRepository mPostRepository;
    private NavigationController mNavigationController;
    private MutableLiveData<NotRedditPostListData> mPostListLiveData;
    private MutableLiveData<Boolean> mEndlessScrollResetLiveData;
    private ObservableBoolean mDataBindIsRefreshing;
    private String mSubredditName = "all";
    private @PostListingSort String mCurrentSort;

    private enum FetchMode {
        START_FRESH,
        ADD_TO_EXISTING_POSTS
    }

    @Inject
    PostListViewModel(PostRepository postRepository) {
        mPostRepository = postRepository;
        mPostListLiveData = new MutableLiveData<>();
        mEndlessScrollResetLiveData = new MutableLiveData<>();
        mDataBindIsRefreshing = new ObservableBoolean();
        mCurrentSort = PostListingSort.HOT;
    }

    LiveData<Boolean> observeResetEndlessScroll() {
        return mEndlessScrollResetLiveData;
    }

    LiveData<NotRedditPostListData> getObservableListing(String subredditName) {
        mSubredditName = subredditName;
        if (mPostListLiveData.getValue() == null) {
            fetchPosts(FetchMode.START_FRESH);
        }
        return mPostListLiveData;
    }

    public void onPostListIdle(int firstVisibleItemPosition) {
        if (firstVisibleItemPosition > 0) {
            NotRedditPostListData postListData = mPostListLiveData.getValue();
            if (postListData == null) {
                return;
            }
            postListData.getPostListing().getPosts().subList(0, firstVisibleItemPosition).clear();
            postListData.setPostsDeletingRange(Range.closedOpen(0, firstVisibleItemPosition));
            mPostListLiveData.postValue(postListData);
        }
    }

    public void onLoadMore() {
        fetchPosts(FetchMode.ADD_TO_EXISTING_POSTS);
    }

    private void fetchPosts(FetchMode fetchMode) {
        mDataBindIsRefreshing.set(true);
        String after = fetchMode == FetchMode.ADD_TO_EXISTING_POSTS ? getCurrentAfter() : null;
        mPostRepository.getPostListing(mSubredditName, mCurrentSort, after)
                .doFinally(() -> mDataBindIsRefreshing.set(false))
                .subscribe(postListing -> this.onNewPostListingReceived(fetchMode, postListing), this::onPostListingFetchError);
    }

    private void onPostListingFetchError(Throwable t) {
        Timber.e(t, "Failed to get a post listing!");
    }

    private void onNewPostListingReceived(FetchMode fetchMode, @NonNull PostListing newPostListing) {
        NotRedditPostListData postListViewData = mPostListLiveData.getValue();
        if (postListViewData == null || fetchMode == FetchMode.START_FRESH) {
            onPostListingStartingFresh(newPostListing);
        } else {
            onPostListingAddToExisting(postListViewData, newPostListing);
        }
        mDataBindIsRefreshing.set(false);
    }

    private void onPostListingStartingFresh(PostListing newPostListing) {
        NotRedditPostListData postListViewData = new NotRedditPostListData();
        postListViewData.setPostListing(newPostListing);
        mPostListLiveData.postValue(postListViewData);
        mEndlessScrollResetLiveData.postValue(true);
    }

    private void onPostListingAddToExisting(NotRedditPostListData postListData, PostListing newPostListing) {
        PostListing existingPostListing = postListData.getPostListing();

        int priorListSize = existingPostListing.getPosts().size();
        int newListSize = newPostListing.getPosts().size() + priorListSize;
        postListData.setPostsChangingRange(Range.closed(priorListSize, newListSize));

        if (postListData.getPostListing().hasPosts()) {
            existingPostListing.addAllPosts(newPostListing.getPosts());
        } else {
            existingPostListing.setPosts(newPostListing.getPosts());
        }
        postListData.getPostListing().setAfter(newPostListing.getAfter());

        mPostListLiveData.postValue(postListData);
        mEndlessScrollResetLiveData.postValue(false);
    }

    @Nullable
    private String getCurrentAfter() {
        if (mPostListLiveData.getValue() != null && mPostListLiveData.getValue().getPostListing() != null) {
            return mPostListLiveData.getValue().getPostListing().getAfter();
        } else {
            return null;
        }
    }

    public void setNavigationController(NavigationController navigationController) {
        mNavigationController = navigationController;
    }

    public void onRefresh() {
        fetchPosts(FetchMode.START_FRESH);
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

    public void onPostSortSelected(@PostListingSort String postListingSort) {
        if (postListingSort.equals(mCurrentSort)) {
            return;
        }
        mCurrentSort = postListingSort;
        fetchPosts(FetchMode.START_FRESH);
    }
}
