package com.stevenbrown.notreddit.di;

import com.stevenbrown.notreddit.ui.main.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Defines dependencies specific to the MainActivity.
 */
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity contributeMainActivity();
}
