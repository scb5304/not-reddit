package com.jollyremedy.notreddit.ui.postdetail;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.di.auto.Injectable;
import com.jollyremedy.notreddit.models.comment.PostWithCommentListing;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.ui.UpNavigationFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostDetailFragment extends Fragment implements Injectable, UpNavigationFragment {

    @BindView(R.id.post_detail_comments_recycler_view)
    RecyclerView mCommentsRecyclerView;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public static final String TAG = "PostDetailFragment";
    public static final String EXTRA_POST = "extra_post";

    private PostDetailViewModel mViewModel;
    private PostDetailAdapter mPostDetailAdapter;

    public static PostDetailFragment newInstance(Post post) {
        PostDetailFragment postDetailFragment = new PostDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_POST, post);
        postDetailFragment.setArguments(args);
        return postDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initRecyclerView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PostDetailViewModel.class);
        subscribeUi();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getPassedPost().getData().getTitle());
    }

    private void initRecyclerView() {
        mPostDetailAdapter = new PostDetailAdapter(getActivity());
        mPostDetailAdapter.setPost(getPassedPost());
        mCommentsRecyclerView.setAdapter(mPostDetailAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mCommentsRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void subscribeUi() {

        mViewModel.getObservablePostWithComments(getPassedPost().getData().getId()).observe(this, postWithCommentListing -> {
            mPostDetailAdapter.setPost(postWithCommentListing.getPostListing().getData().getPosts().get(0));
            mPostDetailAdapter.setComments(postWithCommentListing.getCommentListing().getData().getComments());
            mPostDetailAdapter.notifyDataSetChanged();
        });
    }

    private Post getPassedPost() {
        return getArguments().getParcelable(EXTRA_POST);
    }
}
