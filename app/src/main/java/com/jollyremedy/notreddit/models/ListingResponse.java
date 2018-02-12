package com.jollyremedy.notreddit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListingResponse extends RedditResponse {

    @SerializedName("data")
    protected ListingData listingData;

    public ListingData getListingData() {
        return listingData;
    }
}
