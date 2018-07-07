package com.stevenbrown.notreddit.ui.postlist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.common.collect.Range;
import com.stevenbrown.notreddit.databinding.ItemPostBinding;
import com.stevenbrown.notreddit.models.post.Post;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.SubredditViewHolder> {

    private List<Post> mPosts;
    private PostListViewModel mPostListViewModel;

    void updateData(@NonNull List<Post> posts, @Nullable Range<Integer> changeRange, @Nullable Range<Integer> deleteRange) {
        mPosts = new ArrayList<>(posts);

        if (changeRange != null) {
            Integer numberOfItemsChanging = changeRange.upperEndpoint() - changeRange.lowerEndpoint();
            notifyItemRangeChanged(changeRange.lowerEndpoint(), numberOfItemsChanging);
        } else if (deleteRange != null) {
            Integer numberOfItemsRemoving = deleteRange.upperEndpoint() - deleteRange.lowerEndpoint();
            notifyItemRangeRemoved(deleteRange.lowerEndpoint(), numberOfItemsRemoving);
        } else {
            Timber.w("No change or delete range!");
            notifyDataSetChanged();
        }
    }

    PostListAdapter(PostListViewModel postListViewMode) {
        mPostListViewModel = postListViewMode;
        mPosts = new ArrayList<>();
    }

    @NonNull
    @Override
    public SubredditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemPostBinding itemPostBinding = ItemPostBinding.inflate(layoutInflater, parent, false);
        return new SubredditViewHolder(itemPostBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubredditViewHolder holder, int position) {
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
