package com.stevenbrown.notreddit.models.subreddit;

import android.support.annotation.StringDef;

import java.util.Arrays;
import java.util.List;

import static com.stevenbrown.notreddit.models.subreddit.SubredditForUserWhere.CONTRIBUTOR;
import static com.stevenbrown.notreddit.models.subreddit.SubredditForUserWhere.MODERATOR;
import static com.stevenbrown.notreddit.models.subreddit.SubredditForUserWhere.STREAMS;
import static com.stevenbrown.notreddit.models.subreddit.SubredditForUserWhere.SUBSCRIBER;

@StringDef({SUBSCRIBER, CONTRIBUTOR, MODERATOR, STREAMS})
public @interface SubredditForUserWhere {
    String SUBSCRIBER = "subscriber";
    String CONTRIBUTOR = "contributor";
    String MODERATOR = "moderator";
    String STREAMS = "streams";

    List<String> LIST = Arrays.asList(SUBSCRIBER, CONTRIBUTOR, MODERATOR, STREAMS);
}
