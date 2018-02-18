package com.jollyremedy.notreddit.models.subreddit;

import android.support.annotation.StringDef;

import static com.jollyremedy.notreddit.models.subreddit.SubredditWhere.*;

@StringDef({POPULAR, NEW, GOLD, DEFAULT})
public @interface SubredditWhere {
    String POPULAR = "popular";
    String NEW = "new";
    String GOLD = "gold";
    String DEFAULT = "default";
}
