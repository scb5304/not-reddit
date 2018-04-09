package com.jollyremedy.notreddit.ui.postlist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jollyremedy.notreddit.databinding.ItemPostBinding;
import com.jollyremedy.notreddit.models.post.Post;

import java.util.ArrayList;
import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.SubredditViewHolder> {

    private static final String TAG = "PostAdapter";
    private List<Post> mPosts;
    private PostListViewModel mPostListViewModel;

    void updateData(List<Post> posts) {
        mPosts = posts;
        notifyDataSetChanged();
    }

    PostListAdapter(PostListViewModel postListViewMode) {
        mPostListViewModel = postListViewMode;
        mPosts = new ArrayList<>();
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
            binding.setPostListViewModel(mPostListViewModel);
            binding.executePendingBindings();
        }
    }
}
