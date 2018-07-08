package com.stevenbrown.notreddit.ui.postdetail;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stevenbrown.notreddit.databinding.FragmentPostDetailBinding;
import com.stevenbrown.notreddit.di.auto.Injectable;
import com.stevenbrown.notreddit.models.post.Post;
import com.stevenbrown.notreddit.ui.common.UpNavigationFragment;

import java.util.Objects;

import javax.inject.Inject;

public class PostDetailFragment extends Fragment implements Injectable, UpNavigationFragment {

    private RecyclerView mCommentsRecyclerView;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentPostDetailBinding binding = FragmentPostDetailBinding.inflate(inflater, container, false);
        mCommentsRecyclerView = binding.postDetailCommentsRecyclerView;
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PostDetailViewModel.class);
        subscribeUi();
        initRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).setTitle(getPassedPost().getTitle());
    }

    private void initRecyclerView() {
        mPostDetailAdapter = new PostDetailAdapter(getActivity(), mViewModel, getPassedPost());
        mCommentsRecyclerView.setAdapter(mPostDetailAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mCommentsRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void subscribeUi() {
        mViewModel.getObservablePostDetailData(getPassedPost().getId()).observe(this, this::onPostDetailDataChanged);
        mViewModel.getObservableCommentClick().observe(this, this::onCommentClicked);
    }

    private void onPostDetailDataChanged(PostDetailData postDetailData) {
        mPostDetailAdapter.updateData(postDetailData);
    }

    private void onCommentClicked(CommentClick commentClick) {
        int index;
        if ((index = commentClick.getCurrentSelectedIndex()) != -1) {
            mPostDetailAdapter.notifyItemChanged(index + 1);
        }
        if ((index = commentClick.getNewSelectedIndex()) != -1) {
            mPostDetailAdapter.notifyItemChanged(index + 1);
        }
    }

    private Post getPassedPost() {
        return Objects.requireNonNull(getArguments()).getParcelable(EXTRA_POST);
    }
}
