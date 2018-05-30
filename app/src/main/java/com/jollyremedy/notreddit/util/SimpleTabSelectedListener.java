package com.jollyremedy.notreddit.util;

import android.support.design.widget.TabLayout;

public abstract class SimpleTabSelectedListener implements TabLayout.OnTabSelectedListener {
    public abstract void onTabSelected(TabLayout.Tab tab);

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //Stubbed
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //Stubbed
    }
}
