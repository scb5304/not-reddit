package com.jollyremedy.notreddit.ui.postlist;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.repository.PostRepository;
import com.jollyremedy.notreddit.ui.postlist.PostListViewModel.ListingResponseFetchObserver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import io.reactivex.SingleObserver;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class PostListViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private PostListViewModel mPostListViewModel;

    @Mock
    private PostRepository mPostRepository;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void observablePosts_areNotNull() {
        mPostListViewModel = new PostListViewModel(mPostRepository);
        assertThat(mPostListViewModel.getObservableListing("all"), notNullValue());
    }

    @Test
    public void constructingViewModel_callsRepoToGetsNewPosts() {
        mPostListViewModel = new PostListViewModel(mPostRepository);
        mPostListViewModel.getObservableListing("all").observeForever(mock(Observer.class));
        verify(mPostRepository).getPostListing(any(SingleObserver.class), any(), any(), any());
    }

    @Test
    public void sendsPostsToUi() {
        mPostListViewModel = new PostListViewModel(mPostRepository);
        MutableLiveData<NotRedditPostListData> observablePosts = (MutableLiveData<NotRedditPostListData>) mPostListViewModel.getObservableListing("all");
        List<Post> repoPosts = Collections.nCopies(5, mock(Post.class));
        NotRedditPostListData listingData = mock(NotRedditPostListData.class);

        Observer postObserver = mock(Observer.class);
        observablePosts.observeForever(postObserver);

        observablePosts.postValue(listingData);
        verify(postObserver).onChanged(listingData);
    }

    @Test
    public void swipeToRefresh_getsNewPosts_fresh() {
        mPostListViewModel = new PostListViewModel(mPostRepository);
        reset(mPostRepository);
        ArgumentCaptor<ListingResponseFetchObserver> captor = ArgumentCaptor.forClass(ListingResponseFetchObserver.class);

        mPostListViewModel.onRefresh();
        verify(mPostRepository).getPostListing(captor.capture(), any(), any(), any());
        assertThat(captor.getValue().mFetchMode, is(PostListViewModel.FetchMode.START_FRESH));
    }

    @Test
    public void onLoadMore_getsNewPosts_addOn() {
        mPostListViewModel = new PostListViewModel(mPostRepository);
        reset(mPostRepository);
        ArgumentCaptor<ListingResponseFetchObserver> captor = ArgumentCaptor.forClass(ListingResponseFetchObserver.class);

        mPostListViewModel.onLoadMore();
        verify(mPostRepository).getPostListing(captor.capture(), any(), any(), any());
        assertThat(captor.getValue().mFetchMode, is(PostListViewModel.FetchMode.ADD_TO_EXISTING_POSTS));
    }
}
