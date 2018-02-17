package com.jollyremedy.notreddit.repository;

import android.support.annotation.NonNull;

import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.comment.PostWithCommentListing;

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

    public void getComments(SingleObserver<PostWithCommentListing> observer, @NonNull String postId) {
        mRedditApi.getCommentListing(postId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }
}
