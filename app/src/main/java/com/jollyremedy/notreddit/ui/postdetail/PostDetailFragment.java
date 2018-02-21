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
import com.jollyremedy.notreddit.databinding.FragmentPostDetailBinding;
import com.jollyremedy.notreddit.di.auto.Injectable;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.ui.UpNavigationFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostDetailFragment extends Fragment implements Injectable, UpNavigationFragment {

    @BindView(R.id.post_detail_comments_recycler_view)
    RecyclerView mCommentsRecyclerView;

    @BindView(R.id.nested)
    NestedScrollView mNested;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public static final String TAG = "PostDetailFragment";
    public static final String EXTRA_POST = "extra_post";

    private PostDetailViewModel mViewModel;
    private FragmentPostDetailBinding mBinding;
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
        mBinding = FragmentPostDetailBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mNested.setNestedScrollingEnabled(false);
        mCommentsRecyclerView.setNestedScrollingEnabled(false);
        initRecyclerView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PostDetailViewModel.class);
        mBinding.setPost(getArguments().getParcelable(EXTRA_POST));
        subscribeUi();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getPassedPost().getData().getTitle());
    }

    private void initRecyclerView() {
        mPostDetailAdapter = new PostDetailAdapter();
        mCommentsRecyclerView.setAdapter(mPostDetailAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mCommentsRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void subscribeUi() {
        mBinding.postDetailPostItem.setPost(getPassedPost());
        mBinding.setPostDetailViewModel(mViewModel);
        mViewModel.getObservablePostWithComments(getPassedPost().getData().getId()).observe(this, postWithCommentListing -> {
            mBinding.setPost(postWithCommentListing.getPostListing().getData().getPosts().get(0));
            mPostDetailAdapter.updateData(getActivity(), postWithCommentListing.getCommentListing().getData().getComments());
        });
    }

    private Post getPassedPost() {
        return getArguments().getParcelable(EXTRA_POST);
    }
}
