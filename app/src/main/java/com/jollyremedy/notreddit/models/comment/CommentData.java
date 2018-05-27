package com.jollyremedy.notreddit.models.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.api.adapter.EmptyStringAsNullTypeAdapter;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

class CommentData implements Parcelable {

    /**
     * This nested CommentListing supports the threaded model in case it's being used.
     */
    @JsonAdapter(EmptyStringAsNullTypeAdapter.class)
    @SerializedName("replies")
    public CommentListing replies;

    @SerializedName("author")
    public String author;

    @SerializedName("body")
    public String body;

    @SerializedName("body_html")
    public String bodyHtml;

    @SerializedName("name")
    public String fullName;

    @SerializedName("depth")
    public Integer depth;

    @SerializedName("count")
    public Integer moreCount;

    @SerializedName("score")
    public Integer points;

    @SerializedName("children")
    public List<String> children;

    @SerializedName("created_utc")
    public LocalDateTime createdDateTime;

    @SerializedName("parent_id")
    public String parentId;

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