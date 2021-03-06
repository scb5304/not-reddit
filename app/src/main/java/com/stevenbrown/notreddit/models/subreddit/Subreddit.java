package com.stevenbrown.notreddit.models.subreddit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.stevenbrown.notreddit.models.parent.RedditType;

public class Subreddit extends RedditType implements Parcelable {

    @SerializedName("data")
    private SubredditData data;

    private Subreddit(Parcel in) {
        super(in);
        data = (SubredditData) in.readValue(SubredditData.class.getClassLoader());
    }

    public String getId() {
        return data.id;
    }

    public String getDisplayName() {
        return data.displayName;
    }

    public boolean isSubscribedTo() {
        return data.isSubscribedTo;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(data);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Subreddit> CREATOR = new Parcelable.Creator<Subreddit>() {
        @Override
        public Subreddit createFromParcel(Parcel in) {
            return new Subreddit(in);
        }

        @Override
        public Subreddit[] newArray(int size) {
            return new Subreddit[size];
        }
    };
}