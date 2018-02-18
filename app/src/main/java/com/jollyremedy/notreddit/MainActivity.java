package com.jollyremedy.notreddit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jollyremedy.notreddit.ui.DrawerFragment;
import com.jollyremedy.notreddit.ui.UpNavigationFragment;
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

    @BindView(R.id.activity_main_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_main_drawer_layout)
    DrawerLayout mDrawerLayout;

    @Inject
    DispatchingAndroidInjector<Fragment> mFragmentDispatchingAndroidInjector;

    @Inject
    NavigationController mNavigationController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        initToolbar();

        // Add post list fragment if this is first creation
        if (savedInstanceState == null) {
            mNavigationController.navigateToPostList("leagueoflegends");
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

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return mFragmentDispatchingAndroidInjector;
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
}
