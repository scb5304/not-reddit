package com.jollyremedy.notreddit.models.parent;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public abstract class ListingData implements Parcelable {
    @SerializedName("after")
    protected String after;

    @SerializedName("dist")
    protected int dist;

    @SerializedName("modhash")
    protected String modHash;

    @SerializedName("whitelist_status")
    protected String whitelistStatus;

    public String getAfter() {
        return after;
    }

    public int getDist() {
        return dist;
    }

    public String getModHash() {
        return modHash;
    }

    public String getWhitelistStatus() {
        return whitelistStatus;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    protected ListingData(Parcel in) {
        after = in.readString();
        dist = in.readInt();
        modHash = in.readString();
        whitelistStatus = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(after);
        dest.writeInt(dist);
        dest.writeString(modHash);
        dest.writeString(whitelistStatus);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}