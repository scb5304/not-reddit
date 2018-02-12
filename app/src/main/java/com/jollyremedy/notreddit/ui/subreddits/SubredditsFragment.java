package com.jollyremedy.notreddit.ui.subreddits;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.di.auto.Injectable;
import com.jollyremedy.notreddit.ui.EndlessRecyclerViewScrollListener;
import com.jollyremedy.notreddit.ui.postlist.PostAdapter;
import com.jollyremedy.notreddit.ui.postlist.PostListViewModel;
import com.jollyremedy.notreddit.util.NotRedditViewUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubredditsFragment extends Fragment implements Injectable{

    @BindView(R.id.subreddits_view_pager)
    ViewPager mViewPager;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public static final String TAG = "SubredditsFragment";
    private SubredditsViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subreddits, container, false);
        ButterKnife.bind(this, view);
        mViewPager.setAdapter(new SubredditsPagerAdapter(getChildFragmentManager()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SubredditsViewModel.class);
    }
}
