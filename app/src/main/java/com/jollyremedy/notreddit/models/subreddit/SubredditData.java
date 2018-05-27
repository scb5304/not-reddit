package com.jollyremedy.notreddit.models.subreddit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SubredditData implements Parcelable {

    @SerializedName("id")
    public String id;

    @SerializedName("display_name")
    public String displayName;

    private SubredditData(Parcel in) {
        id = in.readString();
        displayName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(displayName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SubredditData> CREATOR = new Parcelable.Creator<SubredditData>() {
        @Override
        public SubredditData createFromParcel(Parcel in) {
            return new SubredditData(in);
        }

        @Override
        public SubredditData[] newArray(int size) {
            return new SubredditData[size];
        }
    };
}
