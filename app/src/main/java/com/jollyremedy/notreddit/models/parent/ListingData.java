package com.jollyremedy.notreddit.models.parent;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public abstract class ListingData implements Parcelable {
    @SerializedName("after")
    public String after;

    @SerializedName("dist")
    public int dist;

    @SerializedName("modhash")
    public String modHash;

    @SerializedName("whitelist_status")
    public String whitelistStatus;

    public ListingData() {

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