package com.stevenbrown.notreddit.models.parent;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/*
 * A common wrapper that contains a "kind" and "data" field.
 */
public abstract class RedditType implements Parcelable {
    @SerializedName("kind")
    protected Kind kind;

    @SuppressWarnings("unused")
    public enum Kind {
        @SerializedName("Listing") LISTING,
        @SerializedName("more") MORE,
        @SerializedName("t1") COMMENT,
        @SerializedName("t2") ACCOUNT,
        @SerializedName("t3") LINK,
        @SerializedName("t4") MESSAGE,
        @SerializedName("t5") SUBREDDIT,
        @SerializedName("t6") AWARD,
    }

    public RedditType() {

    }

    protected RedditType(Parcel in) {
        kind = (Kind) in.readValue(Kind.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(kind);
    }

    public Kind getKind() {
        return kind;
    }
}