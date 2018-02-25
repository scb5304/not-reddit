package com.jollyremedy.notreddit.ui.common;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.jollyremedy.notreddit.ui.main.MainActivity;
import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.ui.DrawerFragment;
import com.jollyremedy.notreddit.ui.postdetail.PostDetailFragment;
import com.jollyremedy.notreddit.ui.postlist.PostListFragment;

import javax.inject.Inject;

public class NavigationController {
    private MainActivity mMainActivity;
    private final int mContainerId;
    private final FragmentManager mFragmentManager;

    @Inject
    public NavigationController(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mContainerId = R.id.fragment_container;
        mFragmentManager = mainActivity.getSupportFragmentManager();
        mFragmentManager.registerFragmentLifecycleCallbacks(new MainActivityFragmentLifecycleListener(), false);
    }

    public void navigateToPostList(@NonNull String subredditName) {
        PostListFragment fragment = PostListFragment.newInstance(subredditName);
        mFragmentManager.beginTransaction().replace(mContainerId, fragment, PostListFragment.TAG)
                .commit();
    }

    public void navigateToPostDetail(@NonNull Post post) {
        PostDetailFragment fragment = PostDetailFragment.newInstance(post);
        mFragmentManager.beginTransaction()
                .replace(mContainerId, fragment, PostDetailFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Appropriately displays the up-navigation arrow or the drawer icon depending on the fragment
     * being resumed.
     */
    @SuppressWarnings("ConstantConditions")
    private class MainActivityFragmentLifecycleListener extends FragmentManager.FragmentLifecycleCallbacks {
        @Override
        public void onFragmentResumed(FragmentManager fm, Fragment f) {
            super.onFragmentResumed(fm, f);
            if (f instanceof DrawerFragment) {
                mMainActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger);
            } else {
                mMainActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
            }
        }
    }
}
