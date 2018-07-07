package com.stevenbrown.notreddit.ui.postdetail;

import android.content.Context;
import android.content.res.Resources;

import com.stevenbrown.notreddit.R;
import com.stevenbrown.notreddit.util.TimeDiffUtils;

import org.threeten.bp.LocalDateTime;

import javax.inject.Inject;

/**
 * A really simple way to get Android references out of the view model. I could probably have
 * the XML itself fetch resources instead of using these methods but that seems ugly and not testable...
 * and you're not supposed to reference Android packages in the ViewModel itself.... so I have this.
 * Seems okay?
 */
class PostDetailViewModelHelper {

    private Context mContext;
    private Resources mResources;

    @Inject
    PostDetailViewModelHelper(Context context, Resources resources) {
        mContext = context;
        mResources = resources;
    }

    String getDisplayCommentPoints(int points) {
        return mResources.getQuantityString(R.plurals.item_comment_points, points, points);
    }

    String getDisplayCommentTimeSinceCreated(LocalDateTime createdDateTime) {
        return TimeDiffUtils.readableSince(mContext, createdDateTime);
    }
}
