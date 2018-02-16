package com.jollyremedy.notreddit.models.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.RedditType;
import com.jollyremedy.notreddit.models.post.PostListingData;
import com.jollyremedy.notreddit.util.EmptyStringAsNullTypeAdapter;

public class CommentListing extends RedditType implements Parcelable {

    @SerializedName("data")
    private CommentListingData data;

    @Override
    public CommentListingData getData() {
        return data;
    }

    protected CommentListing(Parcel in) {
        super(in);
        data = (CommentListingData) in.readValue(CommentListingData.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(data);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CommentListing> CREATOR = new Parcelable.Creator<CommentListing>() {
        @Override
        public CommentListing createFromParcel(Parcel in) {
            return new CommentListing(in);
        }

        @Override
        public CommentListing[] newArray(int size) {
            return new CommentListing[size];
        }
    };
}