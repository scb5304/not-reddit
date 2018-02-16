package com.jollyremedy.notreddit.ui.postdetail;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.di.auto.Injectable;
import com.jollyremedy.notreddit.models.post.Post;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostDetailFragment extends Fragment implements Injectable {

    @BindView(R.id.post_detail_text)
    TextView mTextView;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public static final String TAG = "PostDetailFragment";
    public static final String EXTRA_POST = "extra_post";

    private PostDetailViewModel mViewModel;

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
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
        ButterKnife.bind(this, view);

        Post post = getArguments().getParcelable(EXTRA_POST);
        mTextView.setText(post.getData().getTitle());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PostDetailViewModel.class);
        subscribeUi();
    }

    private void subscribeUi() {

    }
}
