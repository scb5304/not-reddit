package com.stevenbrown.notreddit.util;

import com.google.android.material.tabs.TabLayout;

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
