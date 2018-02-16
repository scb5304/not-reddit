package com.jollyremedy.notreddit.models.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.ListingData;

import java.util.ArrayList;
import java.util.List;

public final class CommentListingData extends ListingData implements Parcelable {
    @SerializedName("children")
    private List<Comment> comments;

    protected CommentListingData(Parcel in) {
        super(in);
        if (in.readByte() == 0x01) {
            comments = new ArrayList<>();
            in.readList(comments, Comment.class.getClassLoader());
        } else {
            comments = null;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (comments == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(comments);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CommentListingData> CREATOR = new Parcelable.Creator<CommentListingData>() {
        @Override
        public CommentListingData createFromParcel(Parcel in) {
            return new CommentListingData(in);
        }

        @Override
        public CommentListingData[] newArray(int size) {
            return new CommentListingData[size];
        }
    };
}