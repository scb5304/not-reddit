package com.jollyremedy.notreddit.di.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.jollyremedy.notreddit.ui.postlist.PostListViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(PostListViewModel.class)
    abstract ViewModel bindUserViewModel(PostListViewModel userViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(NotRedditViewModelFactory factory);
}
