package com.jollyremedy.notreddit.ui.main;

import android.accounts.AccountManager;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.jollyremedy.notreddit.Constants;
import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.auth.accounting.Accountant;
import com.jollyremedy.notreddit.models.subreddit.Subreddit;
import com.jollyremedy.notreddit.ui.common.DrawerFragment;
import com.jollyremedy.notreddit.ui.common.NavigationController;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector, SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.activity_main_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_main_drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.activity_main_drawer_navigation_view) NavigationView mDrawerNavigationView;

    @Inject
    DispatchingAndroidInjector<Fragment> mFragmentDispatchingAndroidInjector;

    @Inject
    SharedPreferences mSharedPreferences;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
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
            case R.id.menu_post_list_log_in:
                Accountant.getInstance().login(this);
                return true;
            case R.id.menu_post_list_log_out:
                Accountant.getInstance().logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Accountant.CHOOSE_ACCOUNT_REQUEST_CODE) {
            String selectedAccountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            mSharedPreferences.edit()
                    .putString(Constants.SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN, selectedAccountName)
                    .apply();
            refreshUsernameDisplayed();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void subscribeUi() {
        mViewModel.getObservableSubredditListing().observe(this, subredditListing -> {
            Menu menu = mDrawerNavigationView.getMenu();
            menu.clear();
            List<Subreddit> subreddits = subredditListing.getData().getSubreddits();
            for (Subreddit subreddit : subreddits) {
                menu.add(subreddit.getData().getDisplayName());
            }
        });
    }

    private void initDrawer() {
        mDrawerNavigationView.setNavigationItemSelectedListener(item -> {
            mNavigationController.navigateToPostList(item.getTitle().toString());
            mDrawerLayout.closeDrawer(Gravity.START);
            return true;
        });
        refreshUsernameDisplayed();
    }

    private void refreshUsernameDisplayed() {
        String currentUsername = mSharedPreferences.getString(Constants.SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN, null);
        TextView usernameTextView = mDrawerNavigationView
                .getHeaderView(0)
                .findViewById(R.id.drawer_username_textview);
        if (Strings.isNullOrEmpty(currentUsername)) {
            usernameTextView.setVisibility(View.GONE);
        } else {
            usernameTextView.setVisibility(View.VISIBLE);
            usernameTextView.setText(currentUsername);
        }
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Constants.SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN.equals(key)) {
            refreshUsernameDisplayed();
        }
    }
}
