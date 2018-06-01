package com.jollyremedy.notreddit.models.subreddit;

import android.support.annotation.StringDef;

import java.util.Arrays;
import java.util.List;

import static com.jollyremedy.notreddit.models.subreddit.SubredditWhere.DEFAULT;
import static com.jollyremedy.notreddit.models.subreddit.SubredditWhere.GOLD;
import static com.jollyremedy.notreddit.models.subreddit.SubredditWhere.NEW;
import static com.jollyremedy.notreddit.models.subreddit.SubredditWhere.POPULAR;

@StringDef({POPULAR, NEW, GOLD, DEFAULT})
public @interface SubredditWhere {
    String POPULAR = "popular";
    String NEW = "new";
    String GOLD = "gold";
    String DEFAULT = "default";

    List<String> LIST = Arrays.asList(POPULAR, NEW, GOLD, DEFAULT);
}
