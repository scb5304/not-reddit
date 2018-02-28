package com.jollyremedy.notreddit.ui.postdetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.jollyremedy.notreddit.models.comment.Comment;
import com.jollyremedy.notreddit.models.comment.PostWithCommentListing;
import com.jollyremedy.notreddit.models.comment.more.MoreChildren;
import com.jollyremedy.notreddit.models.parent.RedditType;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.repository.CommentRepository;
import com.jollyremedy.notreddit.ui.common.SingleLiveEvent;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class PostDetailViewModel extends ViewModel {
    private static final String TAG = "PostDetailViewModel";
    private CommentRepository mCommentRepository;
    private MutableLiveData<PostDetailData> mPostDetailLiveData;
    private SingleLiveEvent<CommentClick> mCommentClickLiveData;
    private PostDetailViewModelHelper mHelper;
    private Integer mCurrentCommentSelectedIndex;

    @Inject
    PostDetailViewModel(CommentRepository commentRepository, PostDetailViewModelHelper helper) {
        mCommentRepository = commentRepository;
        mHelper = helper;
        mPostDetailLiveData = new MutableLiveData<>();
        mCommentClickLiveData = new SingleLiveEvent<>();
        mCurrentCommentSelectedIndex = -1;
    }

    LiveData<PostDetailData> getObservablePostDetailData(String postId) {
        mCommentRepository.getComments(new PostWithCommentsObserver(), postId);
        return mPostDetailLiveData;
    }

    LiveData<CommentClick> getObservableCommentClick() {
        return mCommentClickLiveData;
    }

    public void onCommentClicked(Comment comment, int commentPosition) {
        mCommentClickLiveData.postValue(new CommentClick(mCurrentCommentSelectedIndex, commentPosition));
        mCurrentCommentSelectedIndex = commentPosition;
    }

    public void onCommentMoreClicked(Comment comment, int commentPosition) {
        Post post = mPostDetailLiveData.getValue().getPost();
        mCommentRepository.getMoreComments(new MoreCommentsObserver(commentPosition), post.getData().getFullName(), comment.getData().getChildren());
    }

    public boolean isCommentClickable(Comment comment) {
        return comment.getKind() != RedditType.Kind.MORE;
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

    private class PostWithCommentsObserver implements SingleObserver<PostWithCommentListing> {
        @Override
        public void onSubscribe(Disposable d) {}

        @Override
        public void onSuccess(PostWithCommentListing postWithCommentListing) {
            PostDetailData postDetailData = new PostDetailData();
            postDetailData.setPost(postWithCommentListing.getPostListing().getData().getPosts().get(0));
            postDetailData.setComments(postWithCommentListing.getCommentListing().getData().getComments());
            mPostDetailLiveData.postValue(postDetailData);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Failed to get post with comments.", e);
        }
    }

    private class MoreCommentsObserver implements SingleObserver<MoreChildren> {
        private int commentPosition;

        MoreCommentsObserver(int commentPosition) {
            this.commentPosition = commentPosition;
        }

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onSuccess(MoreChildren moreChildren) {
            Log.d(TAG, new Gson().toJson(moreChildren));

            PostDetailData postDetailData =  mPostDetailLiveData.getValue();
            postDetailData.clearCommentRanges();
            postDetailData.getComments().remove(this.commentPosition);

            if (moreChildren != null && moreChildren.getJsonWrapper() != null && moreChildren.getJsonWrapper().getData() != null) {
                List<Comment> moreComments = moreChildren.getJsonWrapper().getData().getComments();

                if (moreComments != null && !moreComments.isEmpty()) {
                    postDetailData.getComments().addAll(this.commentPosition, moreComments);
                    postDetailData.setCommentRangeChanging(new Pair<>(this.commentPosition -1, this.commentPosition + moreComments.size()));
                    mPostDetailLiveData.postValue(postDetailData);
                    return;
                }
            }

            postDetailData.setCommentRangeRemoving(new Pair<>(this.commentPosition, this.commentPosition));
            mPostDetailLiveData.postValue(postDetailData);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Failed to get more comments.", e);
        }
    }

}
