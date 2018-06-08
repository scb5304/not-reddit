package com.jollyremedy.notreddit.ui.postlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Strings;
import com.jollyremedy.notreddit.Constants;
import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.databinding.FragmentPostListBinding;
import com.jollyremedy.notreddit.di.auto.Injectable;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.ui.EndlessRecyclerViewScrollListener;
import com.jollyremedy.notreddit.ui.common.DrawerFragment;
import com.jollyremedy.notreddit.ui.common.NavigationController;
import com.jollyremedy.notreddit.ui.main.MainActivity;
import com.jollyremedy.notreddit.util.SimpleTabSelectedListener;
import com.jollyremedy.notreddit.util.Utility;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class PostListFragment extends Fragment implements Injectable, DrawerFragment, SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Inject
    NavigationController mNavigationController;

    public static final String TAG = "PostListFragment";
    private static final String EXTRA_SUBREDDIT_NAME = "extra_subreddit_name";
    private BottomSheetSubredditAdapter mSubredditAdapter;
    private PostListAdapter mPostListAdapter;
    private PostListViewModel mViewModel;
    private EndlessRecyclerViewScrollListener mEndlessScrollListener;
    private FragmentPostListBinding mBinding;

    private RecyclerView mPostRecyclerView;
    private RecyclerView mSubredditRecyclerView;
    private TabLayout mTabLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static PostListFragment newInstance(String subredditName) {
        PostListFragment fragment = new PostListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_SUBREDDIT_NAME, subredditName);
        fragment.setArguments(bundle);
        return fragment;
    }

    private final Observer<NotRedditPostListData> mPostListDataObserver = new Observer<NotRedditPostListData>() {
        @Override
        public void onChanged(@Nullable NotRedditPostListData postListing) {
            if (postListing != null) {
                List<Post> posts = postListing.getPostListing().getPosts();
                mPostListAdapter.updateData(posts, postListing.getPostsChangingRange(), postListing.getPostsDeletingRange());
                postListing.clearRanges();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentPostListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPostRecyclerView = mBinding.postListPostRecyclerView;
        mSubredditRecyclerView = mBinding.postListSubredditRecyclerView;
        mTabLayout = mBinding.postListTabLayout;
        mSwipeRefreshLayout = mBinding.postListSwipeRefreshLayout;

        String[] postSorts = getResources().getStringArray(R.array.post_listing_sorts);
        for (String postSort : postSorts) {
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setText(postSort);
            mTabLayout.addTab(tab);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PostListViewModel.class);
        mViewModel.setNavigationController(mNavigationController);
        mBinding.setPostListViewModel(mViewModel);

        subscribeUi();
        initPostRecyclerView();
        initSubredditRecyclerView();
        initSwipeRefreshLayout();
        initTabLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTitle();
    }

    private void initPostRecyclerView() {
        mPostListAdapter = new PostListAdapter(mViewModel);
        mPostRecyclerView.setAdapter(mPostListAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mPostRecyclerView.setLayoutManager(linearLayoutManager);

        mEndlessScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mViewModel.onLoadMore();
            }
        };

        mPostRecyclerView.addOnScrollListener(mEndlessScrollListener);
        mPostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mViewModel.onPostListIdle(linearLayoutManager.findFirstVisibleItemPosition());
                }
            }
        });
    }

    private void initSubredditRecyclerView() {
        mSubredditAdapter = new BottomSheetSubredditAdapter(mViewModel);
        mSubredditRecyclerView.setAdapter(mSubredditAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mSubredditRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setDistanceToTriggerSync(300);
    }

    private void initTabLayout() {
        mTabLayout.addOnTabSelectedListener(new SimpleTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String postSort = Utility.getPostListingSortFromDisplayedString(getParent(), Objects.requireNonNull(tab.getText()).toString());
                mViewModel.onPostSortSelected(postSort);
                mPostRecyclerView.stopScroll();
            }
        });
    }

    private void subscribeUi() {
        //https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808
        LiveData<NotRedditPostListData> liveData = mViewModel.getObservablePostListing(getSubredditName());
        liveData.removeObserver(mPostListDataObserver);
        liveData.observe(this, mPostListDataObserver);

        mViewModel.observeResetEndlessScroll().observe(this, __ -> {
            mEndlessScrollListener.resetState();
            mPostRecyclerView.postDelayed(() -> mPostRecyclerView.scrollToPosition(0), 100);
        });

        mViewModel.observeCloseBottomSheet().observe(this, __ -> {
            refreshTitle();
            BottomSheetBehavior.from(mBinding.bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        mViewModel.getObservableSubredditListing().observe(this, subreddits -> {
            mSubredditAdapter.updateData(subreddits);
        });
    }

    //FIXME: Not accurate after changing subreddit....
    private String getSubredditName() {
        return Objects.requireNonNull(getArguments()).getString(EXTRA_SUBREDDIT_NAME);
    }

    private void refreshTitle() {
        String title = Strings.isNullOrEmpty(getSubredditName()) ? getString(R.string.app_name) : getSubredditName();
        getParent().setTitle(title);
    }

    private MainActivity getParent() {
        return (MainActivity) Objects.requireNonNull(getActivity());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Constants.SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN.equals(key)) {
            if (sharedPreferences.getBoolean(key, false)) {
                mViewModel.onLoggedIn();
            } else {
                mViewModel.onLoggedOut();
            }
        }
    }
}