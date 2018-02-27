package com.jollyremedy.notreddit.repository;

import android.support.annotation.NonNull;

import com.google.common.base.Joiner;
import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.comment.PostWithCommentListing;
import com.jollyremedy.notreddit.models.comment.more.MoreChildren;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class CommentRepository {

    private static final String TAG = "CommentRepository";
    private OAuthRedditApi mRedditApi;

    @Inject
    CommentRepository(OAuthRedditApi redditApi) {
        mRedditApi = redditApi;
    }

    public void getComments(@NonNull SingleObserver<PostWithCommentListing> observer,
                            @NonNull String postId) {
        mRedditApi.getCommentListing(postId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }

    public void getMoreComments(@NonNull SingleObserver<MoreChildren> observer,
                                @NonNull String postFullName,
                                @NonNull List<String> commentIds) {
        String commaDelimitedCommentIds = Joiner.on(',').join(commentIds);
        mRedditApi.getMoreComments(postFullName, commaDelimitedCommentIds)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }
}
