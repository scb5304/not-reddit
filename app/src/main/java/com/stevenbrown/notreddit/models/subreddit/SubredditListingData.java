package com.stevenbrown.notreddit.models.subreddit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.stevenbrown.notreddit.models.parent.ListingData;

import java.util.ArrayList;
import java.util.List;

public class SubredditListingData extends ListingData implements Parcelable {

    @SerializedName("children")
    public List<Subreddit> subreddits;

    public SubredditListingData() {

    }

    private SubredditListingData(Parcel in) {
        super(in);
        if (in.readByte() == 0x01) {
            subreddits = new ArrayList<>();
            in.readList(subreddits, Subreddit.class.getClassLoader());
        } else {
            subreddits = null;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (subreddits == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(subreddits);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SubredditListingData> CREATOR = new Parcelable.Creator<SubredditListingData>() {
        @Override
        public SubredditListingData createFromParcel(Parcel in) {
            return new SubredditListingData(in);
        }

        @Override
        public SubredditListingData[] newArray(int size) {
            return new SubredditListingData[size];
        }
    };
}