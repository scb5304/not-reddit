package com.jollyremedy.notreddit.models.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.api.adapter.EmptyStringAsNullTypeAdapter;

public class CommentData implements Parcelable {
    @SerializedName("body")
    private String body;

    @JsonAdapter(EmptyStringAsNullTypeAdapter.class)
    @SerializedName("replies")
    private CommentListing replies;

    public String getBody() {
        return body;
    }

    private CommentData(Parcel in) {
        body = in.readString();
        replies = (CommentListing) in.readValue(CommentListing.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeValue(replies);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CommentData> CREATOR = new Parcelable.Creator<CommentData>() {
        @Override
        public CommentData createFromParcel(Parcel in) {
            return new CommentData(in);
        }

        @Override
        public CommentData[] newArray(int size) {
            return new CommentData[size];
        }
    };
}