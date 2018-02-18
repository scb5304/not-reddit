package com.jollyremedy.notreddit.models.post;

import android.support.annotation.StringDef;

import static com.jollyremedy.notreddit.models.post.PostListingSort.*;

@StringDef({HOT, NEW, RANDOM, RISING, TOP, CONTROVERSIAL})
public @interface PostListingSort {
    String HOT = "hot";
    String NEW = "new";
    String RANDOM = "random";
    String RISING = "rising";
    String TOP = "top";
    String CONTROVERSIAL = "controversial";
}
