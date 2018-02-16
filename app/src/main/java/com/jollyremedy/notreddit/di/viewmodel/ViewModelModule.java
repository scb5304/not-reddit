package com.jollyremedy.notreddit.di.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.jollyremedy.notreddit.ui.postlist.PostListViewModel;
import com.jollyremedy.notreddit.ui.postdetail.PostDetailViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(PostListViewModel.class)
    abstract ViewModel bindPostListViewModel(PostListViewModel postListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PostDetailViewModel.class)
    abstract ViewModel bindPostDetailViewModel(PostDetailViewModel postDetailViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(NotRedditViewModelFactory factory);
}
