package com.jollyremedy.notreddit.models.parent;

import com.google.gson.annotations.SerializedName;

public abstract class ListingData {
    @SerializedName("after")
    private String after;

    @SerializedName("dist")
    private int dist;

    @SerializedName("modhash")
    private String modHash;

    @SerializedName("whitelist_status")
    private String whitelistStatus;
}
