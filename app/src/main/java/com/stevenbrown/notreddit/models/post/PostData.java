package com.stevenbrown.notreddit.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PostData implements Parcelable {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("title")
    public String title;

    @SerializedName("selftext")
    public String selfText;

    @SerializedName("is_self")
    public boolean isSelf;

    @SerializedName("selftext_html")
    public String selfTextHtml;

    @SerializedName("url")
    public String url;

    @SerializedName("domain")
    public String domain;

    @SerializedName("subreddit")
    public String subreddit;

    @SerializedName("num_comments")
    public Integer commentCount;

    @SerializedName("score")
    public String score;

    @SerializedName("thumbnail")
    public String thumbnail;

    protected PostData(Parcel in) {
        id = in.readString();
        name = in.readString();
        title = in.readString();
        selfText = in.readString();
        selfTextHtml = in.readString();
        url = in.readString();
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
        dest.writeString(url);
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