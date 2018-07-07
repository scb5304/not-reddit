package com.stevenbrown.notreddit.models.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.stevenbrown.notreddit.models.parent.RedditType;

import org.threeten.bp.LocalDateTime;

import java.util.List;

public class Comment extends RedditType implements Parcelable {

    @SerializedName("data")
    private CommentData data;

    public CommentListing getReplies() {
        return data.replies;
    }

    public String getAuthor() {
        return data.author;
    }

    /**
     * A fullname is a combination of a thing's type (e.g. Comment) and its unique ID which forms a
     * compact encoding of a globally unique ID on reddit.
     */
    public String getFullName() {
        return data.fullName;
    }

    /**
     * Only applicable if this comment data is contained within a {@link RedditType.Kind#MORE} Comment.
     * The number of comments descended from this one that can be the user can choose to fetch.
     */
    public Integer getMoreCount() {
        return data.moreCount;
    }

    /**
     * Only applicable if this CommentData is contained within a {@link RedditType.Kind#MORE} Comment.
     * A comma-delimited list of comment ID36s that need to be fetched.
     */
    public List<String> getChildren() {
        return data.children;
    }

    public String getBody() {
        return data.body;
    }

    public String getBodyHtml() {
        return data.bodyHtml;
    }

    public Integer getDepth() {
        return data.depth;
    }

    public Integer getPoints() {
        return data.points == null ? 0 : data.points;
    }

    public LocalDateTime getCreatedDateTime() {
        return data.createdDateTime;
    }

    public String getParentId() {
        return data.parentId;
    }

    protected Comment(Parcel in) {
        super(in);
        data = (CommentData) in.readValue(CommentData.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(data);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}