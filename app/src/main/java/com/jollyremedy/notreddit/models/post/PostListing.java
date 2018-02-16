package com.jollyremedy.notreddit.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.RedditType;

public class PostListing extends RedditType implements Parcelable {

    @SerializedName("data")
    private PostListingData data;

    @Override
    public PostListingData getData() {
        return data;
    }

    protected PostListing(Parcel in) {
        super(in);
        data = (PostListingData) in.readValue(PostListingData.class.getClassLoader());
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(data);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PostListing> CREATOR = new Parcelable.Creator<PostListing>() {
        @Override
        public PostListing createFromParcel(Parcel in) {
            return new PostListing(in);
        }

        @Override
        public PostListing[] newArray(int size) {
            return new PostListing[size];
        }
    };
}