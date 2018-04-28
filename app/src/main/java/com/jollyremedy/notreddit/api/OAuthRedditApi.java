package com.jollyremedy.notreddit.api;

import com.jollyremedy.notreddit.models.comment.PostWithCommentListing;
import com.jollyremedy.notreddit.models.comment.more.MoreChildren;
import com.jollyremedy.notreddit.models.post.PostListing;
import com.jollyremedy.notreddit.models.subreddit.SubredditForUserWhere;
import com.jollyremedy.notreddit.models.subreddit.SubredditListing;
import com.jollyremedy.notreddit.models.subreddit.SubredditWhere;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OAuthRedditApi {

    @GET("{subredditName}/{sort}?&raw_json=1")
    Single<PostListing> getPostsBySubreddit(@Path(value = "subredditName", encoded = true) String subredditName,
                                            @Path("sort") String sort,
                                            @Query("after") String after);

    @GET("/comments/{postId}?threaded=false&raw_json=1")
    Single<PostWithCommentListing> getCommentListing(@Path("postId") String postId);

    @GET("/subreddits/{where}")
    Single<SubredditListing> getSubredditListingWhere(@Path("where") @SubredditWhere String where);

    @GET("/subreddits/mine/{where}")
    Single<SubredditListing> getSubredditListingForUserWhere(@Path("where") @SubredditForUserWhere String where);

    @GET("/api/morechildren?api_type=json&raw_json=1")
    Single<MoreChildren> getMoreComments(@Query("link_id") String postFullName,
                                         @Query("children") String childrenCommaDelimited);
}
