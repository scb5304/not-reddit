package com.stevenbrown.notreddit.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.stevenbrown.notreddit.models.parent.RedditType;

import java.util.List;

public class PostListing extends RedditType implements Parcelable {

    @SerializedName("data")
    private PostListingData data;

    public List<Post> getPosts() {
        return data.posts;
    }

    public boolean hasPosts() {
        return data.posts != null && !data.posts.isEmpty();
    }

    public void addAllPosts(List<Post> posts) {
        this.data.posts.addAll(posts);
    }

    public void setPosts(List<Post> posts) {
        this.data.posts = posts;
    }

    public String getAfter() {
        return data.after;
    }

    public void setAfter(String after) {
        data.after = after;
    }

    public PostListing() {
        this.data = new PostListingData();
    }

    private PostListing(Parcel in) {
        super(in);
        data = (PostListingData) in.readValue(PostListingData.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(data);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PostListing> CREATOR = new Parcelable.Creator<PostListing>() {
        @Override
        public PostListing createFromParcel(Parcel in) {
            return new PostListing(in);
        }

        @Override
        public PostListing[] newArray(int size) {
            return new PostListing[size];
        }
    };
}