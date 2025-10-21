package com.stevenbrown.notreddit.models.subreddit;

import androidx.annotation.StringDef;

import java.util.Arrays;
import java.util.List;

import static com.stevenbrown.notreddit.models.subreddit.SubredditWhere.DEFAULT;
import static com.stevenbrown.notreddit.models.subreddit.SubredditWhere.GOLD;
import static com.stevenbrown.notreddit.models.subreddit.SubredditWhere.NEW;
import static com.stevenbrown.notreddit.models.subreddit.SubredditWhere.POPULAR;

@StringDef({POPULAR, NEW, GOLD, DEFAULT})
public @interface SubredditWhere {
    String POPULAR = "popular";
    String NEW = "new";
    String GOLD = "gold";
    String DEFAULT = "default";

    List<String> LIST = Arrays.asList(POPULAR, NEW, GOLD, DEFAULT);
}
