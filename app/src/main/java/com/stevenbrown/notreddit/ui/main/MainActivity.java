package com.stevenbrown.notreddit.ui.main;

import android.accounts.AccountManager;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.stevenbrown.notreddit.Constants;
import com.stevenbrown.notreddit.R;
import com.stevenbrown.notreddit.auth.accounting.Accountant;
import com.stevenbrown.notreddit.databinding.ActivityMainBinding;
import com.stevenbrown.notreddit.ui.common.DrawerFragment;
import com.stevenbrown.notreddit.ui.common.NavigationController;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @Inject
    DispatchingAndroidInjector<Fragment> mFragmentDispatchingAndroidInjector;

    @Inject
    SharedPreferences mSharedPreferences;

    @Inject
    NavigationController mNavigationController;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private MainViewModel mViewModel;

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return mFragmentDispatchingAndroidInjector;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mToolbar = binding.activityMainToolbar;
        mDrawerLayout = binding.activityMainDrawerLayout;

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainViewModel.class);
        initToolbar();

        if (savedInstanceState == null) {
            mNavigationController.navigateToPostList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_list, menu);
        boolean loggedIn = mSharedPreferences.getString(Constants.SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN, null) != null;

        MenuItem loginMenuItem = menu.findItem(R.id.menu_post_list_log_in);
        loginMenuItem.setTitle(loggedIn ? getString(R.string.switch_accounts): getString(R.string.log_in));

        MenuItem logoutMenuItem = menu.findItem(R.id.menu_post_list_log_out);
        logoutMenuItem.setVisible(loggedIn);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (closeDrawer() || closeBottomSheet()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return onHomePressed();
            case R.id.menu_post_list_log_in:
                return onLoginPressed();
            case R.id.menu_post_list_log_out:
                return onLogoutPressed();
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
            Toast.makeText(this, getString(R.string.login_success, selectedAccountName), Toast.LENGTH_SHORT).show();
            invalidateOptionsMenu();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean onHomePressed() {
        if (isNavigationDrawerFragmentDisplayed()) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else {
            onBackPressed();
        }
        return true;
    }

    private boolean onLoginPressed() {
        Accountant.getInstance().login(this);
        return true;
    }

    private boolean onLogoutPressed() {
        invalidateOptionsMenu();
        Accountant.getInstance().logout();
        return true;
    }

    private boolean closeDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    private boolean closeBottomSheet() {
        try {
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            //No bottom sheet available, and that's fine.
        }
        return false;
    }


    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        setSupportActionBar(mToolbar);
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
}
