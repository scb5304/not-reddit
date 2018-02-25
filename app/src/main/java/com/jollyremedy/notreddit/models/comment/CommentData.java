package com.jollyremedy.notreddit.models.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.api.adapter.EmptyStringAsNullTypeAdapter;
import com.jollyremedy.notreddit.models.comment.CommentListing;
import com.jollyremedy.notreddit.models.parent.RedditType;
import com.jollyremedy.notreddit.util.TimeDiffUtils;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;

public class CommentData implements Parcelable {

    /**
     * This nested CommentListing supports the threaded model in case it's being used.
     */
    @JsonAdapter(EmptyStringAsNullTypeAdapter.class)
    @SerializedName("replies")
    private CommentListing replies;

    @SerializedName("author")
    private String author;

    @SerializedName("body")
    private String body;

    @SerializedName("body_html")
    private String bodyHtml;

    @SerializedName("name")
    private String fullName;

    @SerializedName("depth")
    private Integer depth;

    @SerializedName("count")
    private Integer moreCount;

    @SerializedName("score")
    private Integer points;

    @SerializedName("children")
    private List<String> children;

    @SerializedName("created_utc")
    private LocalDateTime createdDateTime;

    public String getAuthor() {
        return author;
    }

    /**
     * A fullname is a combination of a thing's type (e.g. Comment) and its unique ID which forms a
     * compact encoding of a globally unique ID on reddit.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Only applicable if this comment data is contained within a {@link RedditType.Kind#MORE} Comment.
     * The number of comments descended from this one that can be the user can choose to fetch.
     */
    public Integer getMoreCount() {
        return moreCount;
    }

    /**
     * Only applicable if this CommentData is contained within a {@link RedditType.Kind#MORE} Comment.
     * A comma-delimited list of comment ID36s that need to be fetched.
     */
    public List<String> getChildren() {
        return children;
    }

    public String getBody() {
        return body;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public Integer getDepth() {
        return depth;
    }

    public Integer getPoints() {
        return points == null ? 0 : points;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    protected CommentData(Parcel in) {
        replies = (CommentListing) in.readValue(CommentListing.class.getClassLoader());
        author = in.readString();
        body = in.readString();
        bodyHtml = in.readString();
        fullName = in.readString();
        depth = in.readByte() == 0x00 ? null : in.readInt();
        moreCount = in.readByte() == 0x00 ? null : in.readInt();
        points = in.readByte() == 0x00 ? null : in.readInt();
        if (in.readByte() == 0x01) {
            children = new ArrayList<String>();
            in.readList(children, String.class.getClassLoader());
        } else {
            children = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(replies);
        dest.writeString(author);
        dest.writeString(body);
        dest.writeString(bodyHtml);
        dest.writeString(fullName);
        if (depth == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(depth);
        }
        if (moreCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(moreCount);
        }
        if (points == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(points);
        }
        if (children == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(children);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CommentData> CREATOR = new Parcelable.Creator<CommentData>() {
        @Override
        public CommentData createFromParcel(Parcel in) {
            return new CommentData(in);
        }

        @Override
        public CommentData[] newArray(int size) {
            return new CommentData[size];
        }
    };
}