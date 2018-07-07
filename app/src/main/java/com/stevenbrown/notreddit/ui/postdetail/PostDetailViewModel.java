package com.stevenbrown.notreddit.ui.postdetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.common.collect.Range;
import com.stevenbrown.notreddit.models.comment.Comment;
import com.stevenbrown.notreddit.models.comment.PostWithCommentListing;
import com.stevenbrown.notreddit.models.comment.more.MoreChildren;
import com.stevenbrown.notreddit.models.parent.RedditType;
import com.stevenbrown.notreddit.models.post.Post;
import com.stevenbrown.notreddit.repository.CommentRepository;
import com.stevenbrown.notreddit.util.SingleLiveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import timber.log.Timber;

public class PostDetailViewModel extends ViewModel {

    private CommentRepository mCommentRepository;
    private MutableLiveData<PostDetailData> mPostDetailLiveData;
    private SingleLiveEvent<CommentClick> mCommentClickLiveData;
    private PostDetailViewModelHelper mHelper;

    private Integer mCurrentCommentSelectedIndex;
    private Map<String, List<Comment>> mCollapsedComments;

    @Inject
    PostDetailViewModel(CommentRepository commentRepository, PostDetailViewModelHelper helper) {
        mCommentRepository = commentRepository;
        mHelper = helper;
        mPostDetailLiveData = new MutableLiveData<>();
        mCommentClickLiveData = new SingleLiveEvent<>();
        mCurrentCommentSelectedIndex = -1;
        mCollapsedComments = new HashMap<>();
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
        Timber.e(throwable, "Failed to get post with comments.");
    }

    private void onMoreCommentsFetched(int moreCommentsPosition, MoreChildren moreChildren) {
        PostDetailData postDetailData = mPostDetailLiveData.getValue();
        if (moreChildren == null || postDetailData == null) {
            mPostDetailLiveData.postValue(postDetailData);
            return;
        }

        List<Comment> moreComments = moreChildren.getComments();

        //TODO: Insert range instead of notifyDataSetChanged
        if (moreComments != null && !moreComments.isEmpty()) {
            postDetailData.getComments().remove(moreCommentsPosition); //Remove "MORE"
            postDetailData.getComments().addAll(moreCommentsPosition, moreComments);
            mPostDetailLiveData.postValue(postDetailData);
        }
    }

    private void onMoreCommentsFailure(Throwable throwable) {
        Timber.e(throwable, "Failed to get more comments.");
    }

    public void onCommentClicked(Comment comment) {
        int index = indexOfComment(comment);
        mCommentClickLiveData.postValue(new CommentClick(mCurrentCommentSelectedIndex, index));
        mCurrentCommentSelectedIndex = index;
    }

    public void onCommentCollapseClicked(Comment comment) {
        onCommentClicked(comment);
        int commentIndex = indexOfComment(comment);

        List<Comment> commentsToCollapse = collapseComments(comment, commentIndex);

        PostDetailData postDetailData = mPostDetailLiveData.getValue();
        postDetailData.getComments().removeAll(commentsToCollapse);
        postDetailData.setCommentsChangingRange(Range.singleton(commentIndex));

        if (!commentsToCollapse.isEmpty()) {
            int deleteRangeStart = commentIndex + 1;
            int deleteRangeEnd = deleteRangeStart + commentsToCollapse.size() - 1;
            postDetailData.setCommentsDeletingRange(Range.closed(deleteRangeStart, deleteRangeEnd));
        }

        mPostDetailLiveData.postValue(postDetailData);
    }

    private List<Comment> collapseComments(Comment comment, int commentIndex) {
        List<Comment> comments = mPostDetailLiveData.getValue().getComments();
        List<Comment> commentsToCollapse = new ArrayList<>();
        int commentDepth = comment.getDepth();
        int loopCommentIndex = commentIndex + 1;

        while (loopCommentIndex < comments.size()) {
            Comment potentialComment = comments.get(loopCommentIndex);
            if (potentialComment.getDepth() > commentDepth) {
                commentsToCollapse.add(potentialComment);
            } else {
                break;
            }
            loopCommentIndex++;
        }

        mCollapsedComments.put(comment.getFullName(), commentsToCollapse);
        return commentsToCollapse;
    }

    public void onCommentMoreClicked(Comment comment) {
        Post post = Objects.requireNonNull(mPostDetailLiveData.getValue()).getPost();

        String postFullName = post.getFullName();
        List<String> commentIdsToGet = comment.getChildren();

        mCommentRepository.getMoreCommentsByIds(postFullName, commentIdsToGet).subscribe(
                moreChildren -> onMoreCommentsFetched(indexOfComment(comment), moreChildren),
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

    public boolean isCommentBottomLineVisible(Comment comment) {
        return Objects.equals(mCurrentCommentSelectedIndex, indexOfComment(comment));
    }

    public boolean isCommentMoreWrapperVisible(Comment comment) {
        return comment.getKind() == RedditType.Kind.MORE;
    }

    public String getCommentPointsText(Comment comment) {
        return mHelper.getDisplayCommentPoints(comment.getPoints());
    }

    public String getCommentBody(Comment comment) {
        if (mCollapsedComments.containsKey(comment.getFullName())) {
            return "(collapsed placeholder)";
        } else {
            return comment.getBodyHtml();
        }
    }

    public String getCommentTimeSince(Comment comment) {
        return mHelper.getDisplayCommentTimeSinceCreated(comment.getCreatedDateTime());
    }

    public String getCommentAuthor(Comment comment) {
        return comment.getAuthor();
    }

    private int indexOfComment(Comment comment) {
        return mPostDetailLiveData.getValue().getComments().indexOf(comment);
    }
}
