package com.jollyremedy.notreddit.ui.postlist;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.jollyremedy.notreddit.BaseUnitTest;
import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.repository.PostRepository;
import com.jollyremedy.notreddit.repository.SubredditRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import io.reactivex.Single;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class PostListViewModelTest extends BaseUnitTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private PostListViewModel mPostListViewModel;

    @Mock
    private SubredditRepository mSubredditRepository;

    @Mock
    private PostRepository mPostRepository;

    @Before
    public void setup() {
        when(mPostRepository.getPostListing(any(), any(), any())).thenReturn(Single.just(mock(PostListing.class)));
    }

    @Test
    public void observablePosts_areNotNull() {
        mPostListViewModel = new PostListViewModel(mSubredditRepository, mPostRepository);
        assertThat(mPostListViewModel.getObservableListing("all"), notNullValue());
    }

    @Test
    public void constructingViewModel_callsRepoToGetsNewPosts() {
        mPostListViewModel = new PostListViewModel(mSubredditRepository, mPostRepository);
        mPostListViewModel.getObservableListing("all").observeForever(mock(Observer.class));
        verify(mPostRepository).getPostListing(any(), any(), any());
    }

    @Test
    public void sendsPostsToUi() {
        mPostListViewModel = new PostListViewModel(mSubredditRepository, mPostRepository);
        MutableLiveData<NotRedditPostListData> observablePosts = (MutableLiveData<NotRedditPostListData>) mPostListViewModel.getObservableListing("all");
        NotRedditPostListData listingData = mock(NotRedditPostListData.class);

        Observer postObserver = mock(Observer.class);
        observablePosts.observeForever(postObserver);
        observablePosts.postValue(listingData);
        verify(postObserver).onChanged(listingData);
    }
}
