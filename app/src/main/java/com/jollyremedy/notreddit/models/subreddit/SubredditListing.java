package com.jollyremedy.notreddit.models.subreddit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.RedditType;

import java.util.ArrayList;
import java.util.List;

public class SubredditListing extends RedditType {

    @SerializedName("data")
    private SubredditListingData data;

    public List<Subreddit> getSubreddits() {
        return data.subreddits;
    }

    private SubredditListing(Parcel in) {
        super(in);
        data = (SubredditListingData) in.readValue(SubredditListingData.class.getClassLoader());
    }

    public SubredditListing() {
        this.data = new SubredditListingData();
        this.data.subreddits = new ArrayList<>();
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

    public boolean hasSubreddit(Subreddit subreddit) {
        for (Subreddit aSubreddit : getSubreddits()) {
            if (aSubreddit.getDisplayName().equals(subreddit.getDisplayName())) {
                return true;
            }
        }
        return false;
    }

    public void addListing(SubredditListing listing) {
        List<Subreddit> subredditsToAdd = listing.getSubreddits();
        for (Subreddit subreddit : subredditsToAdd) {
            if (!SubredditListing.this.hasSubreddit(subreddit)) {
                this.data.subreddits.add(subreddit);
            }
        }

    }
}
