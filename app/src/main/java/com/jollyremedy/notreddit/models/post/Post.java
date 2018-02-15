package com.jollyremedy.notreddit.models.post;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jollyremedy.notreddit.models.parent.RedditType;

import org.threeten.bp.LocalDateTime;

public class Post extends RedditType {
    @SerializedName("data")
    private PostData data;

    @Override
    public PostData getData() {
        return data;
    }
}
