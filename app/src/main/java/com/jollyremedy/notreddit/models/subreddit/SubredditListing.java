package com.jollyremedy.notreddit.models.subreddit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.RedditType;

public class SubredditListing extends RedditType {

    @SerializedName("data")
    private SubredditListingData data;

    @Override
    public SubredditListingData getData() {
        return data;
    }

    private SubredditListing(Parcel in) {
        super(in);
        data = (SubredditListingData) in.readValue(SubredditListingData.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(data);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SubredditListing> CREATOR = new Parcelable.Creator<SubredditListing>() {
        @Override
        public SubredditListing createFromParcel(Parcel in) {
            return new SubredditListing(in);
        }

        @Override
        public SubredditListing[] newArray(int size) {
            return new SubredditListing[size];
        }
    };
}
