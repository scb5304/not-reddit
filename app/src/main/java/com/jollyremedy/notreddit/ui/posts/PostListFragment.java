package com.jollyremedy.notreddit.ui.posts;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.di.Injectable;
import com.jollyremedy.notreddit.models.ListingResponse;
import com.jollyremedy.notreddit.models.Post;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.AndroidSupportInjection;

public class PostListFragment extends Fragment implements Injectable,
        SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.post_list_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.post_list_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public static final String TAG = "PostListFragment";
    private PostAdapter mPostAdapter;
    private PostListViewModel mViewModel;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        ButterKnife.bind(this, view);

        mPostAdapter = new PostAdapter();
        mRecyclerView.setAdapter(mPostAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setDistanceToTriggerSync(300);
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
        mViewModel.onSwipeToRefresh();
    }

    @OnClick(R.id.test_button)
    void onTestButtonClicked() {
        mViewModel.onTestButtonClicked();
    }

    private void subscribeUi() {
        mViewModel.getListingResponse().observe(this, listingResponse -> {
            mSwipeRefreshLayout.setRefreshing(false);
            if (listingResponse == null) {
                Log.i(TAG, "The view got a null listing response.");
                mPostAdapter.updateData(new ArrayList<>());
            } else {
                Log.i(TAG, "The view got a listing response: " + new Gson().toJson(listingResponse));
                mPostAdapter.updateData(listingResponse.getListingData().getPosts());
            }
        });
    }
}