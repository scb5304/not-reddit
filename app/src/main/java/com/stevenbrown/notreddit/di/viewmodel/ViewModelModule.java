package com.stevenbrown.notreddit.di.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.stevenbrown.notreddit.ui.main.MainViewModel;
import com.stevenbrown.notreddit.ui.postdetail.PostDetailViewModel;
import com.stevenbrown.notreddit.ui.postlist.PostListViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindMainViewModel(MainViewModel mainViewModel);

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
