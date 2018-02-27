package com.jollyremedy.notreddit.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

public class PostData implements Parcelable {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("title")
    private String title;

    @SerializedName("selftext")
    private String selfText;

    @SerializedName("selftext_html")
    private String selfTextHtml;

    @SerializedName("domain")
    private String domain;

    @SerializedName("subreddit")
    private String subreddit;

    @SerializedName("num_comments")
    private Integer commentCount;

    @SerializedName("score")
    private String score;

    @SerializedName("thumbnail")
    private String thumbnail;

    public boolean hasSelfText() {
        return !Strings.isNullOrEmpty(selfText);
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getSelfText() {
        return selfText;
    }

    public String getSelfTextHtml() {
        return selfTextHtml;
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

    public String getScore() {
        return score;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    protected PostData(Parcel in) {
        id = in.readString();
        name = in.readString();
        title = in.readString();
        selfText = in.readString();
        selfTextHtml = in.readString();
        domain = in.readString();
        subreddit = in.readString();
        commentCount = in.readByte() == 0x00 ? null : in.readInt();
        score = in.readString();
        thumbnail = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(title);
        dest.writeString(selfText);
        dest.writeString(selfTextHtml);
        dest.writeString(domain);
        dest.writeString(subreddit);
        if (commentCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(commentCount);
        }
        dest.writeString(score);
        dest.writeString(thumbnail);
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