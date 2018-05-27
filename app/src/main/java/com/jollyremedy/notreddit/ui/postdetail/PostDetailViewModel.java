package com.jollyremedy.notreddit.ui.postdetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.jollyremedy.notreddit.models.comment.Comment;
import com.jollyremedy.notreddit.models.comment.PostWithCommentListing;
import com.jollyremedy.notreddit.models.comment.more.MoreChildren;
import com.jollyremedy.notreddit.models.parent.RedditType;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.repository.CommentRepository;
import com.jollyremedy.notreddit.util.SingleLiveEvent;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

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
        mCommentRepository.getCommentsWithPostId(postId).subscribe(
                this::onPostWithCommentListingFetched,
                this::onPostWithCommentListingFailure);
        return mPostDetailLiveData;
    }

    SingleLiveEvent<CommentClick> getObservableCommentClick() {
        return mCommentClickLiveData;
    }

    private void onPostWithCommentListingFetched(PostWithCommentListing postWithCommentListing) {
        PostDetailData postDetailData = new PostDetailData();
        postDetailData.setPost(postWithCommentListing.getPostListing().getPosts().get(0));
        postDetailData.setComments(postWithCommentListing.getCommentListing().getComments());
        mPostDetailLiveData.postValue(postDetailData);
    }

    private void onPostWithCommentListingFailure(Throwable throwable) {
        Log.e(TAG, "Failed to get post with comments.", throwable);
    }

    private void onMoreCommentsFetched(int moreCommentsPosition, MoreChildren moreChildren) {
        PostDetailData postDetailData = mPostDetailLiveData.getValue();
        if (moreChildren == null || postDetailData == null) {
            mPostDetailLiveData.postValue(postDetailData);
            return;
        }

        List<Comment> moreComments = moreChildren.getComments();

        if (moreComments != null && !moreComments.isEmpty()) {
            postDetailData.getComments().remove(moreCommentsPosition); //Remove "MORE"
            postDetailData.getComments().addAll(moreCommentsPosition, moreComments);
            mPostDetailLiveData.postValue(postDetailData);
        }
    }

    private void onMoreCommentsFailure(Throwable throwable) {
        Log.e(TAG, "Failed to get more comments.", throwable);
    }

    public void onCommentClicked(int commentPosition) {
        mCommentClickLiveData.postValue(new CommentClick(mCurrentCommentSelectedIndex, commentPosition));
        mCurrentCommentSelectedIndex = commentPosition;
    }

    public void onCommentMoreClicked(Comment comment, int commentPosition) {
        Post post = mPostDetailLiveData.getValue().getPost();

        String postFullName = post.getFullName();
        List<String> commentIdsToGet = comment.getChildren();

        mCommentRepository.getMoreCommentsByIds(postFullName, commentIdsToGet).subscribe(
                moreChildren -> onMoreCommentsFetched(commentPosition, moreChildren),
                this::onMoreCommentsFailure);
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
        return mHelper.getDisplayCommentPoints(comment.getPoints());
    }

    public String getCommentBody(Comment comment) {
        return comment.getBodyHtml();
    }

    public String getCommentTimeSince(Comment comment) {
        return mHelper.getDisplayCommentTimeSinceCreated(comment.getCreatedDateTime());
    }

    public String getCommentAuthor(Comment comment) {
        return comment.getAuthor();
    }
}
