package com.jollyremedy.notreddit.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.RedditType;

public class PostData implements Parcelable {

    @SerializedName("title")
    private String title;

    @SerializedName("domain")
    private String domain;

    @SerializedName("subreddit")
    private String subreddit;

    @SerializedName("num_comments")
    private Integer commentCount;

    public String getTitle() {
        return title;
    }

    public String getDomain() {
        return domain;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    private PostData(Parcel in) {
        title = in.readString();
        domain = in.readString();
        subreddit = in.readString();
        commentCount = in.readByte() == 0x00 ? null : in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(domain);
        dest.writeString(subreddit);
        if (commentCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(commentCount);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PostData> CREATOR = new Parcelable.Creator<PostData>() {
        @Override
        public PostData createFromParcel(Parcel in) {
            return new PostData(in);
        }

        @Override
        public PostData[] newArray(int size) {
            return new PostData[size];
        }
    };
}