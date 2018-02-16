package com.jollyremedy.notreddit.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.RedditType;

public final class Post extends RedditType implements Parcelable {
    @SerializedName("data")
    private PostData data;

    @Override
    public PostData getData() {
        return data;
    }

    private Post(Parcel in) {
        super(in);
        data = (PostData) in.readValue(PostData.class.getClassLoader());
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