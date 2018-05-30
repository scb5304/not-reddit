package com.jollyremedy.notreddit.models.post;

import android.support.annotation.StringDef;

import static com.jollyremedy.notreddit.models.post.PostListingSort.BEST;
import static com.jollyremedy.notreddit.models.post.PostListingSort.CONTROVERSIAL;
import static com.jollyremedy.notreddit.models.post.PostListingSort.HOT;
import static com.jollyremedy.notreddit.models.post.PostListingSort.NEW;
import static com.jollyremedy.notreddit.models.post.PostListingSort.RISING;
import static com.jollyremedy.notreddit.models.post.PostListingSort.TOP;

@StringDef({HOT, NEW, BEST, RISING, TOP, CONTROVERSIAL})
public @interface PostListingSort {
    String HOT = "hot";
    String NEW = "new";
    String BEST = "best";
    String RISING = "rising";
    String TOP = "top";
    String CONTROVERSIAL = "controversial";
}
