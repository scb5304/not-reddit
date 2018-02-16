package com.jollyremedy.notreddit.ui.common;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.jollyremedy.notreddit.MainActivity;
import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.ui.postlist.PostListFragment;
import com.jollyremedy.notreddit.ui.postlist.postdetail.PostDetailFragment;

import javax.inject.Inject;

public class NavigationController {
    private final int mContainerId;
    private final FragmentManager mFragmentManager;
    private static final String TEST_SUBREDDIT_NAME = "leagueoflegends";

    @Inject
    public NavigationController(MainActivity mainActivity) {
        mContainerId = R.id.fragment_container;
        mFragmentManager = mainActivity.getSupportFragmentManager();
    }

    public void navigateToPostList(@NonNull String subreddit) {
        PostListFragment fragment = PostListFragment.newInstance(TEST_SUBREDDIT_NAME);
        mFragmentManager.beginTransaction().replace(mContainerId, fragment, PostListFragment.TAG)
                .commit();
    }

    public void navigateToPostDetail(@NonNull Post post) {
        PostDetailFragment fragment = PostDetailFragment.newInstance(post);
        mFragmentManager.beginTransaction().replace(mContainerId, fragment, PostDetailFragment.TAG)
                .addToBackStack(null)
                .commit();
    }
}
