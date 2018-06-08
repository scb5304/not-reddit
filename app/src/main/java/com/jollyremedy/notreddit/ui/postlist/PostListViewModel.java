package com.jollyremedy.notreddit.ui.postlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.Range;
import com.jollyremedy.notreddit.auth.accounting.Accountant;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.models.post.PostListingSort;
import com.jollyremedy.notreddit.models.subreddit.Subreddit;
import com.jollyremedy.notreddit.models.subreddit.SubredditForUserWhere;
import com.jollyremedy.notreddit.models.subreddit.SubredditListing;
import com.jollyremedy.notreddit.models.subreddit.SubredditWhere;
import com.jollyremedy.notreddit.repository.PostRepository;
import com.jollyremedy.notreddit.repository.SubredditRepository;
import com.jollyremedy.notreddit.ui.common.NavigationController;
import com.jollyremedy.notreddit.util.SingleLiveEvent;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import timber.log.Timber;

public class PostListViewModel extends ViewModel {

    private SubredditRepository mSubredditRepository;
    private PostRepository mPostRepository;
    private NavigationController mNavigationController;

    private MutableLiveData<NotRedditPostListData> mPostListLiveData;
    private MutableLiveData<List<Subreddit>> mSubredditLiveData;
    private SingleLiveEvent<Object> mEndlessScrollResetLiveData;
    private SingleLiveEvent<Object> mCloseBottomSheetLiveData;

    private ObservableBoolean mDataBindIsRefreshing;
    private String mSubredditName = "all";
    private @PostListingSort String mCurrentSort;

    private enum FetchMode {
        START_FRESH,
        ADD_TO_EXISTING_POSTS
    }

    @Inject
    PostListViewModel(SubredditRepository subredditRepository, PostRepository postRepository) {
        mSubredditRepository = subredditRepository;
        mPostRepository = postRepository;
        mCloseBottomSheetLiveData = new SingleLiveEvent<>();
        mPostListLiveData = new MutableLiveData<>();
        mEndlessScrollResetLiveData = new SingleLiveEvent<>();
        mDataBindIsRefreshing = new ObservableBoolean();
        mCurrentSort = PostListingSort.HOT;
    }

    SingleLiveEvent<Object> observeCloseBottomSheet() {
        return mCloseBottomSheetLiveData;
    }

    LiveData<Object> observeResetEndlessScroll() {
        return mEndlessScrollResetLiveData;
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

    public void onLoggedIn() {
        fetchSubreddits();
    }

    public void onLoggedOut() {
        fetchSubreddits();
    }

    //region Posts
    LiveData<NotRedditPostListData> getObservablePostListing(String subredditName) {
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
        mEndlessScrollResetLiveData.call();
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
        mEndlessScrollResetLiveData.call();
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
    //endregion

    //region Subreddits
    LiveData<List<Subreddit>> getObservableSubredditListing() {
        if (mSubredditLiveData == null) {
            mSubredditLiveData = new MutableLiveData<>();
            fetchSubreddits();
        }
        return mSubredditLiveData;
    }

    private void fetchSubreddits() {
        Single<SubredditListing> fetchSingle;

        if (Accountant.getInstance().getCurrentAccessToken() != null) {
            List<String> subredditWheres = Collections.singletonList(SubredditWhere.DEFAULT);
            List<String> subredditForUserWheres = Collections.singletonList(SubredditForUserWhere.SUBSCRIBER);
            fetchSingle = mSubredditRepository.getSubredditsForParams(subredditWheres, subredditForUserWheres);
        } else {
            fetchSingle = mSubredditRepository.getSubredditsWhere(SubredditWhere.DEFAULT);
        }

        fetchSingle.subscribe(this::onSubredditListingReceived, this::onSubredditListingFetchError);
    }

    public void onSubredditClicked(Subreddit subreddit) {
        mSubredditName = subreddit.getDisplayName();
        fetchPosts(FetchMode.START_FRESH);
        mCloseBottomSheetLiveData.call();
    }

    private void onSubredditListingReceived(SubredditListing subredditListing) {
        mSubredditLiveData.postValue(subredditListing.getSubreddits());
    }

    private void onSubredditListingFetchError(Throwable t) {
        Timber.e(t, "Failed to get a subreddit listing!");
    }
    //endregion
}
