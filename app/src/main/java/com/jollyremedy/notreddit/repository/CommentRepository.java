package com.jollyremedy.notreddit.repository;

import android.support.annotation.NonNull;

import com.google.common.base.Joiner;
import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.comment.PostWithCommentListing;
import com.jollyremedy.notreddit.models.comment.more.MoreChildren;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CommentRepository {

    private static final String TAG = "CommentRepository";
    private OAuthRedditApi mRedditApi;

    @Inject
    CommentRepository(OAuthRedditApi redditApi) {
        mRedditApi = redditApi;
    }

    public Single<PostWithCommentListing> getCommentsWithPostId(@NonNull String postId) {
        return mRedditApi.getCommentListing(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MoreChildren> getMoreCommentsByIds(@NonNull String postFullName, @NonNull List<String> commentIds) {
        String commaDelimitedCommentIds = Joiner.on(',').join(commentIds);
        return mRedditApi.getMoreComments(postFullName, commaDelimitedCommentIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
