package com.jollyremedy.notreddit.ui.postdetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.jollyremedy.notreddit.models.comment.Comment;
import com.jollyremedy.notreddit.models.comment.PostWithCommentListing;
import com.jollyremedy.notreddit.models.parent.RedditType;
import com.jollyremedy.notreddit.repository.CommentRepository;
import com.jollyremedy.notreddit.ui.common.SingleLiveEvent;

import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class PostDetailViewModel extends ViewModel {
    private static final String TAG = "PostDetailViewModel";
    private CommentRepository mCommentRepository;
    private MutableLiveData<PostWithCommentListing> mPostWithCommentsLiveData;
    private SingleLiveEvent<CommentClick> mCommentClickLiveData;
    private PostDetailViewModelHelper mHelper;
    private Integer mCurrentCommentSelectedIndex;

    @Inject
    PostDetailViewModel(CommentRepository commentRepository, PostDetailViewModelHelper helper) {
        mCommentRepository = commentRepository;
        mHelper = helper;
        mPostWithCommentsLiveData = new MutableLiveData<>();
        mCommentClickLiveData = new SingleLiveEvent<>();
        mCurrentCommentSelectedIndex = -1;
    }

    LiveData<PostWithCommentListing> getObservablePostWithComments(String postId) {
        mCommentRepository.getComments(new PostWithCommentsObserver(), postId);
        return mPostWithCommentsLiveData;
    }

    LiveData<CommentClick> getObservableCommentClick() {
        return mCommentClickLiveData;
    }

    private class PostWithCommentsObserver implements SingleObserver<PostWithCommentListing> {
        @Override
        public void onSubscribe(Disposable d) {}

        @Override
        public void onSuccess(PostWithCommentListing postWithCommentListing) {
            mPostWithCommentsLiveData.postValue(postWithCommentListing);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Oh no.", e);
        }
    }

    public void onCommentClicked(Comment comment, int commentPosition) {
        mCommentClickLiveData.postValue(new CommentClick(mCurrentCommentSelectedIndex, commentPosition));
        mCurrentCommentSelectedIndex = commentPosition;
        Log.i(TAG, "You clicked comment " + comment.getData().getFullName());
    }

    public boolean isCommentBodyVisible(Comment comment) {
        return comment.getKind() != RedditType.Kind.MORE;
    }

    public boolean isCommentTopLineVisible(Comment comment) {
        return comment.getKind() != RedditType.Kind.MORE;
    }

    public boolean isCommentBottomLineVisible(Integer commentIndex) {
        return Objects.equals(mCurrentCommentSelectedIndex, commentIndex);
    }

    public boolean isCommentMoreWrapperVisible(Comment comment) {
        return comment.getKind() == RedditType.Kind.MORE;
    }

    public String getCommentPointsText(Comment comment) {
        return mHelper.getDisplayCommentPoints(comment.getData().getPoints());
    }

    public String getCommentBody(Comment comment) {
        return comment.getData().getBodyHtml();
    }

    public String getCommentTimeSince(Comment comment) {
        return mHelper.getDisplayCommentTimeSinceCreated(comment.getData().getCreatedDateTime());
    }

    public String getCommentAuthor(Comment comment) {
        return comment.getData().getAuthor();
    }
}
