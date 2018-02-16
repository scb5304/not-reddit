package com.jollyremedy.notreddit.models.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.RedditType;

public class Comment extends RedditType implements Parcelable {
    @SerializedName("data")
    private CommentData data;

    @Override
    public CommentData getData() {
        return data;
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