package com.jollyremedy.notreddit.ui.postlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.databinding.ItemPostBinding;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.models.post.PostData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.SubredditViewHolder> {

    private static final String TAG = "PostAdapter";
    private List<Post> mPosts;
    private PostClickedCallback mPostClickedCallback;

    void updateData(List<Post> posts) {
        mPosts = posts;
        notifyDataSetChanged();
    }

    PostAdapter(PostClickedCallback postClickedCallback) {
        mPostClickedCallback = postClickedCallback;
        mPosts = new ArrayList<>();
    }

    public interface PostClickedCallback {
        void onPostClicked(Post post);
    }

    @Override
    public SubredditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemPostBinding itemPostBinding = ItemPostBinding.inflate(layoutInflater, parent, false);
        return new SubredditViewHolder(itemPostBinding);
    }

    @Override
    public void onBindViewHolder(SubredditViewHolder holder, int position) {
        holder.bind(mPosts.get(position));
    }

    @Override
    public int getItemCount() {
        return mPosts == null ? 0 : mPosts.size();
    }

    class SubredditViewHolder extends RecyclerView.ViewHolder {
        private final ItemPostBinding binding;

        SubredditViewHolder(ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Post post) {
            binding.setPost(post);
            binding.setPostClickedCallback(mPostClickedCallback);
            binding.executePendingBindings();
        }
    }
}
