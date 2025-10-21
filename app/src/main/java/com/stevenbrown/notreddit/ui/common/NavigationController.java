package com.stevenbrown.notreddit.ui.common;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.stevenbrown.notreddit.R;
import com.stevenbrown.notreddit.models.post.Post;
import com.stevenbrown.notreddit.ui.main.MainActivity;
import com.stevenbrown.notreddit.ui.postdetail.PostDetailFragment;
import com.stevenbrown.notreddit.ui.postlist.PostListFragment;
import com.stevenbrown.notreddit.util.NotRedditViewUtils;

import javax.inject.Inject;

public class NavigationController {
    private MainActivity mMainActivity;
    private final int mContainerId;
    private final FragmentManager mFragmentManager;

    //TODO: really?
    public NavigationController(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mContainerId = R.id.fragment_container;
        mFragmentManager = mainActivity.getSupportFragmentManager();
        mFragmentManager.registerFragmentLifecycleCallbacks(new MainActivityFragmentLifecycleListener(), false);
    }

    public void navigateToPostList() {
        PostListFragment fragment = PostListFragment.newInstance();
        mFragmentManager.beginTransaction()
                .replace(mContainerId, fragment, PostListFragment.TAG)
                .commit();
    }

    /**
     * Navigates to either the post's web page, if exists, or its comments page.
     */
    public void navigateToPostGeneric(@NonNull Post post) {
        if (post.hasSelfText()) {
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
        navigateToWebPage(post.getUrl());
    }

    public void navigateToWebPage(@NonNull String url) {
        CustomTabsIntent customTabsIntent = NotRedditViewUtils.createBaseCustomTabsIntent(mMainActivity);
        //TODO: androidx.browser
//        CustomTabsHelper.addKeepAliveExtra(mMainActivity, customTabsIntent.intent);
//        CustomTabsHelper.openCustomTab(mMainActivity, customTabsIntent,
//                Uri.parse(url),
//                new WebViewFallback());
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
