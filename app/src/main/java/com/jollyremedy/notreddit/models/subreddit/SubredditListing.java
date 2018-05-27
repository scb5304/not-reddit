package com.jollyremedy.notreddit.models.subreddit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.RedditType;

import java.util.List;

public class SubredditListing extends RedditType {

    @SerializedName("data")
    private SubredditListingData data;

    public List<Subreddit> getSubreddits() {
        return data.getSubreddits();
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
