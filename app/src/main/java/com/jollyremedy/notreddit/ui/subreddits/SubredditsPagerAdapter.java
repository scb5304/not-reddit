package com.jollyremedy.notreddit.ui.subreddits;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.jollyremedy.notreddit.ui.postlist.PostListFragment;

public class SubredditsPagerAdapter extends FragmentPagerAdapter {

    public SubredditsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        String subredditName = "";
        switch (position) {
            case 0:
                subredditName = "leagueoflegends";
                break;
            case 1:
                subredditName = "hearthstone";
                break;
            case 2:
                subredditName = "games";
                break;
        }
        return PostListFragment.newInstance(subredditName);
    }

    @Override
    public int getCount() {
        return 3;
    }
}
