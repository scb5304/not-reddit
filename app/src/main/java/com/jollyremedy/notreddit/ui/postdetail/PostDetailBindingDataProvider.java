package com.jollyremedy.notreddit.ui.postdetail;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.google.common.base.Strings;
import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.models.comment.Comment;
import com.jollyremedy.notreddit.models.parent.RedditType;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.util.TimeDiffUtils;

import org.threeten.bp.LocalDateTime;

import java.util.List;

/**
 * An attempt to make the data binding logic testable and not crammed into the Adapter itself either.
 */
public class PostDetailBindingDataProvider {

    private Context mContext;
    private Resources mResources;

    private List<Comment> mAllComments;
    private Comment mComment;
    private int mCurrentCommentPosition;

    PostDetailBindingDataProvider(@NonNull Context context) {
        mContext = context;
        mResources = mContext.getResources();
    }

    void setAllComments(@NonNull List<Comment> allComments) {
        mAllComments = allComments;
    }

    void setCurrentCommentPosition(int commentPosition) {
        mCurrentCommentPosition = commentPosition;
        mComment = mAllComments.get(commentPosition);
    }

    public boolean isRootIconVisible() {
        return mComment.getData().getDepth() == 0;
    }

    public boolean isCommentBodyVisible() {
        return mComment.getKind() != RedditType.Kind.MORE;
    }

    public boolean isCommentTopLineVisible() {
        return mComment.getKind() != RedditType.Kind.MORE;
    }

    public boolean isCommentMoreWrapperVisible() {
        return mComment.getKind() == RedditType.Kind.MORE;
    }

    @SuppressWarnings("unused")
    public boolean isCommentCollapseVisible() {
        if (mAllComments.size() > mCurrentCommentPosition + 1) {
            Comment nextComment = mAllComments.get(mCurrentCommentPosition + 1);
            String currentCommentName = Strings.emptyToNull(mComment.getData().getFullName());
            return currentCommentName.equals(nextComment.getData().getParentId());
        }
        return false;
    }

    public String getCommentPointsText() {
        int commentPoints = mComment.getData().getPoints();
        return mResources.getQuantityString(R.plurals.item_comment_points, commentPoints, commentPoints);
    }

    public String getCommentBody() {
        return mComment.getData().getBodyHtml();
    }

    public String getCommentTimeSince() {
        LocalDateTime createdDateTime = mComment.getData().getCreatedDateTime();
        return TimeDiffUtils.readableSince(mContext, createdDateTime);
    }

    public String getCommentAuthor() {
        return mComment.getData().getAuthor();
    }
}
