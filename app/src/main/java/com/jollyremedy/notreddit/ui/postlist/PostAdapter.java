package com.jollyremedy.notreddit.ui.postlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.models.post.PostData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.SubredditViewHolder> {

    private static final String TAG = "PostAdapter";
    private List<Post> mPosts;
    private Context mContext;

    void updateData(List<Post> posts) {
        mPosts = posts;
        notifyDataSetChanged();
    }

    PostAdapter(Context context) {
        mContext = context;
        mPosts = new ArrayList<>();
    }

    @Override
    public SubredditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new SubredditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubredditViewHolder holder, int position) {
        PostData postData = mPosts.get(position).getData();
        holder.postTitleTextView.setText(postData.getTitle());
        holder.postSubredditTextView.setText(mContext.getString(R.string.subreddit_with_prefix, postData.getSubreddit()));
        holder.postCommentCountTextView.setText(mContext.getString(R.string.item_post_comment_count, postData.getCommentCount()));
        holder.postDomainTextView.setText(mContext.getString(R.string.item_post_domain, postData.getDomain()));
    }

    @Override
    public int getItemCount() {
        return mPosts == null ? 0 : mPosts.size();
    }

    class SubredditViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_post_image) ImageView postImageView;
        @BindView(R.id.item_post_title) TextView postTitleTextView;
        @BindView(R.id.item_post_subreddit) TextView postSubredditTextView;
        @BindView(R.id.item_post_comment_count) TextView postCommentCountTextView;
        @BindView(R.id.item_post_domain) TextView postDomainTextView;

        SubredditViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
