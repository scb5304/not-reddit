package com.stevenbrown.notreddit.ui.postlist;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.common.base.Strings;
import com.stevenbrown.notreddit.Constants;
import com.stevenbrown.notreddit.R;
import com.stevenbrown.notreddit.databinding.FragmentPostListBinding;
import com.stevenbrown.notreddit.models.post.Post;
import com.stevenbrown.notreddit.ui.EndlessRecyclerViewScrollListener;
import com.stevenbrown.notreddit.ui.common.DrawerFragment;
import com.stevenbrown.notreddit.ui.common.NavigationController;
import com.stevenbrown.notreddit.ui.main.MainActivity;
import com.stevenbrown.notreddit.util.SimpleTabSelectedListener;
import com.stevenbrown.notreddit.util.Utility;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostListFragment extends Fragment implements DrawerFragment {

    @Inject
    PostListViewModel mViewModel;

    NavigationController mNavigationController;

    @Inject
    SharedPreferences mSharedPreferences;

    public static final String TAG = "PostListFragment";
    private static final String EXTRA_SUBREDDIT_NAME = "extra_subreddit_name";
    private BottomSheetSubredditAdapter mSubredditAdapter;
    private PostListAdapter mPostListAdapter;
    private EndlessRecyclerViewScrollListener mEndlessScrollListener;
    private FragmentPostListBinding mBinding;

    private RecyclerView mPostRecyclerView;
    private RecyclerView mSubredditRecyclerView;
    private TabLayout mTabLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //TODO
    public static final String DEFAULT_SUBREDDIT = "all";

    public static PostListFragment newInstance() {
        PostListFragment fragment = new PostListFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    private final Observer<NotRedditPostListData> mPostListDataObserver = new Observer<>() {
        @Override
        public void onChanged(@Nullable NotRedditPostListData postListData) {
            if (postListData != null) {
                refreshTitle(postListData.getCurrentSubreddit());
                if (postListData.getPostListing() != null) {
                    List<Post> posts = postListData.getPostListing().getPosts();
                    mPostListAdapter.updateData(posts, postListData.getPostsChangingRange(), postListData.getPostsDeletingRange());
                    postListData.clearRanges();
                }
            }
        }
    };

    private final SharedPreferences.OnSharedPreferenceChangeListener mSharedPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (Constants.SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN.equals(key)) {
                if (sharedPreferences.getString(key, null) != null) {
                    mViewModel.onLoggedIn();
                } else {
                    mViewModel.onLoggedOut();
                }
            }
        }
    };

    private void refreshTitle(String title) {
        if (Strings.isNullOrEmpty(title)) {
            getParent().setTitle(getString(R.string.app_name));
        } else {
            getParent().setTitle(title);
        }
    }

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
        mBinding.fab.setOnClickListener(__ -> onFabClicked());

        String[] postSorts = getResources().getStringArray(R.array.post_listing_sorts);
        for (String postSort : postSorts) {
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setText(postSort);
            mTabLayout.addTab(tab);
        }
    }

    public void onFabClicked() {
        if (BottomSheetBehavior.from(mBinding.bottomSheet).getState() == BottomSheetBehavior.STATE_EXPANDED) {
            BottomSheetBehavior.from(mBinding.bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            BottomSheetBehavior.from(mBinding.bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNavigationController = new NavigationController((MainActivity) requireActivity());
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
        mSharedPreferences.registerOnSharedPreferenceChangeListener(mSharedPrefListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mSharedPrefListener);
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
        mSubredditAdapter = new BottomSheetSubredditAdapter(getActivity(), mViewModel);
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
        LiveData<NotRedditPostListData> liveData = mViewModel.getObservablePostListing(DEFAULT_SUBREDDIT);
        liveData.removeObserver(mPostListDataObserver);
        liveData.observe(getViewLifecycleOwner(), mPostListDataObserver);

        mViewModel.observeResetEndlessScroll().observe(getViewLifecycleOwner(), __ -> {
            mEndlessScrollListener.resetState();
            mPostRecyclerView.postDelayed(() -> mPostRecyclerView.scrollToPosition(0), 100);
        });

        mViewModel.observePostClicked().observe(getViewLifecycleOwner(), post -> {
            mNavigationController.navigateToPostGeneric(post);
        });
        mViewModel.observePostCommentsClicked().observe(getViewLifecycleOwner(), post -> {
            mNavigationController.navigateToPostDetail(post);
        });

        mViewModel.observeCloseBottomSheet().observe(getViewLifecycleOwner(), __ -> {
            BottomSheetBehavior.from(mBinding.bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
            mBinding.postListSubredditRecyclerView.postDelayed(() ->  mBinding.postListSubredditRecyclerView.scrollToPosition(0), 100);
        });

        mViewModel.getObservableSubredditListing().observe(getViewLifecycleOwner(), subreddits -> {
            mSubredditAdapter.updateData(subreddits);
        });
    }

    private MainActivity getParent() {
        return (MainActivity) requireActivity();
    }
}