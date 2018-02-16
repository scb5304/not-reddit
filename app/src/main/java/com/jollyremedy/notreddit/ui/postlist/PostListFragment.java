package com.jollyremedy.notreddit.ui.postlist;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.databinding.FragmentPostListBinding;
import com.jollyremedy.notreddit.di.auto.Injectable;
import com.jollyremedy.notreddit.models.comment.CommentListing;
import com.jollyremedy.notreddit.ui.EndlessRecyclerViewScrollListener;
import com.jollyremedy.notreddit.util.NotRedditViewUtils;
import com.jollyremedy.notreddit.util.Utility;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PostListFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Inject
    Gson mGson;

    @Inject
    OAuthRedditApi mRedditApi;


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
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setDistanceToTriggerSync(300);
    }

    private void subscribeUi() {
        String subredditName = getArguments().getString(EXTRA_SUBREDDIT_NAME);
        mViewModel.getObservableListing(subredditName).observe(this, postListing -> {
            if (postListing != null) {
                mPostAdapter.updateData(postListing.getData().getPosts());
                if (getActivity() != null) {
                    getActivity().setTitle(subredditName);
                }
            }
        });
        mViewModel.observeResetEndlessScroll().observe(this, shouldReset -> {
            if (shouldReset != null && shouldReset) {
                mEndlessScrollListener.resetState();
            }
        });
    }
}