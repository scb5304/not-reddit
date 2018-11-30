package com.stevenbrown.notreddit.di;

import com.stevenbrown.notreddit.ui.postdetail.PostDetailFragment;
import com.stevenbrown.notreddit.ui.postlist.PostListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Binds all sub-components in the app. Prevents us from creating separate subcomponents annotated
 * with @Subcomponent. Included inside the {@link MainActivityModule}, which is a module of the
 * {@link AppComponent}.
 */
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract PostListFragment contributePostListFragment();

    @ContributesAndroidInjector
    abstract PostDetailFragment contributePostDetailFragment();
}
