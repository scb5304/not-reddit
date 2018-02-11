package com.jollyremedy.notreddit.ui.posts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.models.Post;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.SubredditViewHolder> {

    private List<Post> mPosts;

    void updateData(List<Post> posts) {
        mPosts = posts;
        notifyDataSetChanged();
    }

    PostAdapter() {
        mPosts = new ArrayList<>();
    }

    @Override
    public SubredditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new SubredditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubredditViewHolder holder, int position) {
        holder.postTitleTextView.setText(mPosts.get(position).getTitle());
        holder.postTimeTextView.setText(String.valueOf(mPosts.get(position).getCreatedDateTime()));
    }

    @Override
    public int getItemCount() {
        return mPosts == null ? 0 : mPosts.size();
    }

    class SubredditViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_post_title) TextView postTitleTextView;
        @BindView(R.id.item_post_time) TextView postTimeTextView;

        SubredditViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
