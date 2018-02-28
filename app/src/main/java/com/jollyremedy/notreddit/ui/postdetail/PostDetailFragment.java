package com.jollyremedy.notreddit.ui.postdetail;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.di.auto.Injectable;
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
        getActivity().setTitle(getPassedPost().getData().getTitle());
    }

    private void initRecyclerView() {
        mPostDetailAdapter = new PostDetailAdapter(getActivity(), mViewModel, getPassedPost());
        mCommentsRecyclerView.setAdapter(mPostDetailAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mCommentsRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void subscribeUi() {
        mViewModel.getObservablePostDetailData(getPassedPost().getData().getId()).observe(this, postDetailData -> {
            mPostDetailAdapter.setPost(postDetailData.getPost());
            mPostDetailAdapter.setComments(postDetailData.getComments());

            if (postDetailData.getCommentRangeRemoving() != null) {
                Pair<Integer, Integer> commentRangeRemoving = postDetailData.getCommentRangeRemoving();
                mPostDetailAdapter.notifyItemRangeRemoved(commentRangeRemoving.first - 1, commentRangeRemoving.second - 1);
            } else if (postDetailData.getCommentRangeChanging() != null) {
                Pair<Integer, Integer> commentRangeChanging = postDetailData.getCommentRangeChanging();
                mPostDetailAdapter.notifyItemRangeChanged(commentRangeChanging.first - 1, commentRangeChanging.second - 1);
            } else {
                mPostDetailAdapter.notifyDataSetChanged();
            }
        });
        mViewModel.getObservableCommentClick().observe(this, (@NonNull CommentClick commentClick) -> {
            int index;
            if ((index = commentClick.getCurrentSelectedIndex()) != -1) {
                mPostDetailAdapter.notifyItemChanged(index + 1);
            }
            if ((index = commentClick.getNewSelectedIndex()) != -1) {
                mPostDetailAdapter.notifyItemChanged(index + 1);
            }
        });
    }

    private Post getPassedPost() {
        return getArguments().getParcelable(EXTRA_POST);
    }
}
