package com.jollyremedy.notreddit.models.parent;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/*
 * A common wrapper that contains a "kind" and "data" field.
 */
public abstract class RedditType implements Parcelable {
    @SerializedName("kind")
    protected RedditTypePrefix kind;

    @SuppressWarnings("unused")
    public enum RedditTypePrefix {
        t1, //Comment
        t2, //Account
        t3, //Link
        t4, //Message
        t5, //Subreddit
        t6  //Award
    }

    public abstract Object getData();

    protected RedditType(Parcel in) {
        kind = (RedditTypePrefix) in.readValue(RedditTypePrefix.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(kind);
    }
}