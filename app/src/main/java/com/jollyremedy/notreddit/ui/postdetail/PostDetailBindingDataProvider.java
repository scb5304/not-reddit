package com.jollyremedy.notreddit.ui.postdetail;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.models.comment.Comment;
import com.jollyremedy.notreddit.models.parent.RedditType;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.util.TimeDiffUtils;

import org.threeten.bp.LocalDateTime;

/**
 * An attempt to make the data binding logic testable and not crammed into the Adapter itself either.
 */
public class PostDetailBindingDataProvider {

    private Context mContext;
    private Resources mResources;
    private Post mPost;
    private Comment mComment;

    public PostDetailBindingDataProvider(@NonNull Context context,
                                         @NonNull Post post,
                                         @NonNull Comment comment) {
        mContext = context;
        mResources = mContext.getResources();
        mPost = post;
        mComment = comment;
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
