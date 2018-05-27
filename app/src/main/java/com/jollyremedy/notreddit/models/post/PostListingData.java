package com.jollyremedy.notreddit.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.ListingData;

import java.util.ArrayList;
import java.util.List;

public class PostListingData extends ListingData implements Parcelable {

    @SerializedName("children")
    public List<Post> posts;

    private PostListingData(Parcel in) {
        super(in);
        if (in.readByte() == 0x01) {
            posts = new ArrayList<>();
            in.readList(posts, Post.class.getClassLoader());
        } else {
            posts = null;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (posts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(posts);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PostListingData> CREATOR = new Parcelable.Creator<PostListingData>() {
        @Override
        public PostListingData createFromParcel(Parcel in) {
            return new PostListingData(in);
        }

        @Override
        public PostListingData[] newArray(int size) {
            return new PostListingData[size];
        }
    };
}