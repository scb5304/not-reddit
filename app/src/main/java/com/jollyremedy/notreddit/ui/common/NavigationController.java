package com.jollyremedy.notreddit.ui.common;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.ui.main.MainActivity;
import com.jollyremedy.notreddit.ui.postdetail.PostDetailFragment;
import com.jollyremedy.notreddit.ui.postlist.PostListFragment;
import com.jollyremedy.notreddit.util.NotRedditViewUtils;

import javax.inject.Inject;

import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

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

    /**
     * Navigates to either the post's web page, if exists, or its comments page.
     */
    public void navigateToPostGeneric(@NonNull Post post) {
        if (post.getData().hasSelfText()) {
            navigateToPostDetail(post);
        } else {
            openPostLink(post);
        }
    }

    /**
     * Navigates directly to the post's comments page.
     */
    public void navigateToPostDetail(@NonNull Post post) {
        PostDetailFragment fragment = PostDetailFragment.newInstance(post);
        mFragmentManager.beginTransaction()
                .replace(mContainerId, fragment, PostDetailFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    private void openPostLink(@NonNull Post post) {
        navigateToWebPage(post.getData().getUrl());
    }

    public void navigateToWebPage(@NonNull String url) {
        CustomTabsIntent customTabsIntent = NotRedditViewUtils.createBaseCustomTabsIntent(mMainActivity);
        CustomTabsHelper.addKeepAliveExtra(mMainActivity, customTabsIntent.intent);
        CustomTabsHelper.openCustomTab(mMainActivity, customTabsIntent,
                Uri.parse(url),
                new WebViewFallback());
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
