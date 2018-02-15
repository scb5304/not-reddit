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

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.di.auto.Injectable;
import com.jollyremedy.notreddit.ui.EndlessRecyclerViewScrollListener;
import com.jollyremedy.notreddit.util.NotRedditViewUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostListFragment extends Fragment implements Injectable,
        SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.post_list_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.post_list_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public static final String TAG = "PostListFragment";
    private static final String EXTRA_SUBREDDIT_NAME = "extra_subreddit_name";
    private PostAdapter mPostAdapter;
    private PostListViewModel mViewModel;
    private EndlessRecyclerViewScrollListener mEndlessScrollListener;

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
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();
        initSwipeRefreshLayout();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PostListViewModel.class);
        subscribeUi();
    }

    @Override
    public void onRefresh() {
        mEndlessScrollListener.resetState();
        mViewModel.onSwipeToRefresh();
    }

    private void initRecyclerView() {
        mPostAdapter = new PostAdapter(getActivity());
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
        //NotRedditViewUtils.applyHorizontalItemDecorationToRecyclerView(getActivity(), mRecyclerView);
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setDistanceToTriggerSync(300);
    }

    private void subscribeUi() {
        String subredditName = getArguments().getString(EXTRA_SUBREDDIT_NAME);
        mViewModel.getObservableListing(subredditName).observe(this, postListing -> {
            if (postListing != null) {
                mSwipeRefreshLayout.setRefreshing(false);
                mPostAdapter.updateData(postListing.getData().getPosts());
                if (getActivity() != null) {
                    getActivity().setTitle(subredditName);
                }
            }
        });
    }
}