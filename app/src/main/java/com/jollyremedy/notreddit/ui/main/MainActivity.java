package com.jollyremedy.notreddit.ui.main;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.models.subreddit.Subreddit;
import com.jollyremedy.notreddit.ui.DrawerFragment;
import com.jollyremedy.notreddit.ui.common.NavigationController;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @BindView(R.id.activity_main_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_main_drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.activity_main_drawer_navigation_view) NavigationView mDrawerNavigationView;

    @Inject
    DispatchingAndroidInjector<Fragment> mFragmentDispatchingAndroidInjector;

    @Inject
    NavigationController mNavigationController;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private static final String TAG = "MainActivity";
    private MainViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainViewModel.class);
        initDrawer();
        initToolbar();
        subscribeUi();

        if (savedInstanceState == null) {
            mNavigationController.navigateToPostList("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isNavigationDrawerFragmentDisplayed()) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                } else {
                    onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void subscribeUi() {
        mViewModel.getObservableSubredditListing().observe(this, subredditListing -> {
            Menu menu = mDrawerNavigationView.getMenu();
            List<Subreddit> subreddits = subredditListing.getData().getSubreddits();
            for (Subreddit subreddit : subreddits) {
                menu.add(subreddit.getData().getDisplayName());
            }
        });
    }

    private void initDrawer() {
        mDrawerNavigationView.setNavigationItemSelectedListener(item -> {
            mNavigationController.navigateToPostList(item.getTitle().toString());
            return true;
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean isNavigationDrawerFragmentDisplayed() {
        List<Fragment> attachedFragments = getSupportFragmentManager().getFragments();
        for (Fragment attachedFragment : attachedFragments) {
            if (attachedFragment instanceof DrawerFragment && attachedFragment.isVisible()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return mFragmentDispatchingAndroidInjector;
    }
}
