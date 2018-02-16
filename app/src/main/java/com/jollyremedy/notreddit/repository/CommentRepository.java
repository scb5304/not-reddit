package com.jollyremedy.notreddit.repository;

import android.support.annotation.NonNull;

import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.comment.CommentListing;

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

    public void getComments(SingleObserver<List<CommentListing>> observer, @NonNull String subredditName, @NonNull String postId) {
        mRedditApi.getCommentListing(subredditName, postId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }
}
