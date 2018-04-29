package com.jollyremedy.notreddit.models.subreddit;

import android.support.annotation.StringDef;

import static com.jollyremedy.notreddit.models.subreddit.SubredditForUserWhere.CONTRIBUTOR;
import static com.jollyremedy.notreddit.models.subreddit.SubredditForUserWhere.MODERATOR;
import static com.jollyremedy.notreddit.models.subreddit.SubredditForUserWhere.STREAMS;
import static com.jollyremedy.notreddit.models.subreddit.SubredditForUserWhere.SUBSCRIBER;

@StringDef({SUBSCRIBER, CONTRIBUTOR, MODERATOR, STREAMS})
public @interface SubredditForUserWhere {
    String SUBSCRIBER = "subscriber";
    String CONTRIBUTOR = "contributor";
    String MODERATOR = "moderator";
    String STREAMS = "streams";
}