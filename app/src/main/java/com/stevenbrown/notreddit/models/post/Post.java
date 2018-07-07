package com.stevenbrown.notreddit.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;
import com.stevenbrown.notreddit.models.parent.RedditType;

public class Post extends RedditType implements Parcelable {

    @SerializedName("data")
    private PostData data;

    private Post(Parcel in) {
        super(in);
        data = (PostData) in.readValue(PostData.class.getClassLoader());
    }

    public boolean hasSelfText() {
        return !Strings.isNullOrEmpty(data.selfText);
    }

    public String getId() {
        return data.id;
    }

    public String getFullName() {
        return data.name;
    }

    public String getTitle() {
        return data.title;
    }

    public String getSelfText() {
        return data.selfText;
    }

    public String getSelfTextHtml() {
        return data.selfTextHtml;
    }

    public String getUrl() {
        return data.url;
    }

    public String getDomain() {
        return data.domain;
    }

    public String getSubreddit() {
        return data.subreddit;
    }

    public Integer getCommentCount() {
        return data.commentCount;
    }

    public String getScore() {
        return data.score;
    }

    public String getThumbnail() {
        return data.thumbnail;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(data);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}