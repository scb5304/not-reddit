package com.stevenbrown.notreddit.ui.postdetail;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stevenbrown.notreddit.databinding.FragmentPostDetailBinding;
import com.stevenbrown.notreddit.di.auto.Injectable;
import com.stevenbrown.notreddit.models.post.Post;
import com.stevenbrown.notreddit.ui.common.NavigationController;
import com.stevenbrown.notreddit.ui.common.UpNavigationFragment;
import com.stevenbrown.notreddit.ui.main.MainActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostDetailFragment extends Fragment implements Injectable, UpNavigationFragment {

    public static final String TAG = "PostDetailFragment";
    public static final String EXTRA_POST = "extra_post";

    @Inject
    PostDetailViewModel mViewModel;

    NavigationController mNavigationController;

    private RecyclerView mCommentsRecyclerView;

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
        mNavigationController = new NavigationController((MainActivity) requireActivity());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        subscribeUi();
        initRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setTitle(getPassedPost().getTitle());
    }

    private void initRecyclerView() {
        mPostDetailAdapter = new PostDetailAdapter(getActivity(), mViewModel, getPassedPost());
        mCommentsRecyclerView.setAdapter(mPostDetailAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mCommentsRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void subscribeUi() {
        mViewModel.getObservablePostDetailData(getPassedPost().getId()).observe(getViewLifecycleOwner(), this::onPostDetailDataChanged);
        mViewModel.getObservableCommentClick().observe(getViewLifecycleOwner(), this::onCommentClicked);
        mViewModel.getObservableLinkClicked().observe(getViewLifecycleOwner(), this::onLinkClicked);
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

    private void onLinkClicked(String href) {
        mNavigationController.navigateToWebPage(href);
    }

    private Post getPassedPost() {
        return requireArguments().getParcelable(EXTRA_POST);
    }
}
