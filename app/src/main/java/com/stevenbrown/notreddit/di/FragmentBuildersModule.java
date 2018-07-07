/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
