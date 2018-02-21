package com.jollyremedy.notreddit.ui.postlist;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.databinding.FragmentPostListBinding;
import com.jollyremedy.notreddit.di.auto.Injectable;
import com.jollyremedy.notreddit.ui.DrawerFragment;
import com.jollyremedy.notreddit.ui.EndlessRecyclerViewScrollListener;
import com.jollyremedy.notreddit.ui.UpNavigationFragment;
import com.jollyremedy.notreddit.ui.common.NavigationController;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostListFragment extends Fragment implements Injectable, DrawerFragment {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Inject
    Gson mGson;

    @Inject
    NavigationController mNavigationController;

    @BindView(R.id.post_list_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.post_list_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static final String TAG = "PostListFragment";
    private static final String EXTRA_SUBREDDIT_NAME = "extra_subreddit_name";
    private PostAdapter mPostAdapter;
    private PostListViewModel mViewModel;
    private EndlessRecyclerViewScrollListener mEndlessScrollListener;
    private FragmentPostListBinding mBinding;

    public static PostListFragment newInstance(String subredditName) {
        PostListFragment fragment = new PostListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_SUBREDDIT_NAME, subredditName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentPostListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initRecyclerView();
        initSwipeRefreshLayout();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PostListViewModel.class);
        mBinding.setPostListViewModel(mViewModel);
        subscribeUi();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTitle();
    }

    private void initRecyclerView() {
        mPostAdapter = new PostAdapter(post -> mNavigationController.navigateToPostDetail(post));
        mRecyclerView.setAdapter(mPostAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mEndlessScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mViewModel.onLoadMore();
            }
        };
        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setDistanceToTriggerSync(300);
    }

    private void subscribeUi() {
        mViewModel.getObservableListing(getSubredditName()).observe(this, postListing -> {
            if (postListing != null) {
                mPostAdapter.updateData(postListing.getData().getPosts());
            }
        });
        mViewModel.observeResetEndlessScroll().observe(this, shouldReset -> {
            if (shouldReset != null && shouldReset) {
                mEndlessScrollListener.resetState();
            }
        });
    }

    private String getSubredditName() {
        return getArguments().getString(EXTRA_SUBREDDIT_NAME);
    }

    private void refreshTitle() {
        getActivity().setTitle(Strings.isNullOrEmpty(getSubredditName()) ? getString(R.string.app_name) : getSubredditName());
    }
}