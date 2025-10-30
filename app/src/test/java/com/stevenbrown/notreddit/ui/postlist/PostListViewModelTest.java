package com.stevenbrown.notreddit.ui.postlist;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.stevenbrown.notreddit.BaseUnitTest;
import com.stevenbrown.notreddit.RxTestHelper;
import com.stevenbrown.notreddit.auth.accounting.Accountant;
import com.stevenbrown.notreddit.models.post.Post;
import com.stevenbrown.notreddit.models.post.PostListing;
import com.stevenbrown.notreddit.models.subreddit.Subreddit;
import com.stevenbrown.notreddit.repository.PostRepository;
import com.stevenbrown.notreddit.repository.SubredditRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"ConstantConditions", "unchecked"})
@RunWith(JUnit4.class)
public class PostListViewModelTest extends BaseUnitTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private PostListViewModel mPostListViewModel;

    @Mock
    private Accountant mAccountant;

    @Mock
    private SubredditRepository mSubredditRepository;

    @Mock
    private PostRepository mPostRepository;

    @Before
    public void setup() {
        mPostRepository = mock(PostRepository.class, RxTestHelper.defaultRepositoryAnswer());
        mSubredditRepository = mock(SubredditRepository.class, RxTestHelper.defaultRepositoryAnswer());
        mPostListViewModel = new PostListViewModel(mAccountant, mSubredditRepository, mPostRepository);
    }

    private void prepareMockPostListing() {
        PostListing postListing = new PostListing();
        List<Post> posts = new ArrayList<>(Collections.nCopies(25, mock(Post.class)));
        postListing.setPosts(posts);

        NotRedditPostListData listingData = new NotRedditPostListData();
        listingData.setPostListing(postListing);

        MutableLiveData<NotRedditPostListData> observablePosts = (MutableLiveData<NotRedditPostListData>) mPostListViewModel.getObservablePostListing("all");
        observablePosts.postValue(listingData);
    }

    @Test
    public void observablePosts_areNotNull() {
        assertThat(mPostListViewModel.getObservablePostListing("all"), notNullValue());
    }

    @Test
    public void observableCloseBottomSheet_isNotNull() {
        assertThat(mPostListViewModel.observeCloseBottomSheet(), notNullValue());
    }

    @Test
    public void observableResetEndlessScroll_isNotNull() {
        assertThat(mPostListViewModel.observeResetEndlessScroll(), notNullValue());
    }

    @Test
    public void observingPostsFirstTime_callsRepoToGetsNewPosts() {
        when(mPostRepository.getPostListing(any(), any(), any())).thenReturn(Single.just(mock(PostListing.class)));
        mPostListViewModel.getObservablePostListing("all").observeForever(mock(Observer.class));
        mPostListViewModel.getObservablePostListing("all").observeForever(mock(Observer.class));
        verify(mPostRepository, times(1)).getPostListing(any(), any(), any());
    }

    @Test
    public void postListRefreshed_doesNotKeepOldPosts() {
        prepareMockPostListing();

        PostListing freshPostListing = new PostListing();
        List<Post> freshPosts = Collections.nCopies(13, mock(Post.class));
        freshPostListing.setPosts(freshPosts);
        when(mPostRepository.getPostListing(any(), any(), any())).thenReturn(Single.just(freshPostListing));

        mPostListViewModel.onRefresh();
        int postSize = mPostListViewModel.getObservablePostListing("").getValue().getPostListing().getPosts().size();
        assertEquals(postSize, 13);
    }

    @Test
    public void postListLoadMore_keepsOldPosts() {
        prepareMockPostListing();

        PostListing freshPostListing = new PostListing();
        List<Post> freshPosts = Collections.nCopies(13, mock(Post.class));
        freshPostListing.setPosts(freshPosts);
        when(mPostRepository.getPostListing(any(), any(), any())).thenReturn(Single.just(freshPostListing));

        mPostListViewModel.onLoadMore();
        int postSize = mPostListViewModel.getObservablePostListing("").getValue().getPostListing().getPosts().size();
        assertEquals(postSize, 38);
    }

    @Test
    public void userLoggedIn_fetchesSubreddits() {
        when(mAccountant.getCurrentAccessToken()).thenReturn("123");

        mPostListViewModel.onLoggedIn();
        verify(mSubredditRepository).getSubredditsForParams(anyList(), anyList());
    }

    @Test
    public void subredditClicked_reloadsPosts() {
        mPostListViewModel.onSubredditClicked(mock(Subreddit.class));
        verify(mPostRepository).getPostListing(any(), any(), any());
    }
}
