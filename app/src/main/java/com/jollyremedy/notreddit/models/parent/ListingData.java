package com.jollyremedy.notreddit.models.parent;

import com.google.gson.annotations.SerializedName;

public abstract class ListingData {
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
}
