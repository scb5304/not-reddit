package com.jollyremedy.notreddit.ui.postlist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jollyremedy.notreddit.databinding.ItemSubredditBinding;
import com.jollyremedy.notreddit.models.subreddit.Subreddit;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetSubredditAdapter extends RecyclerView.Adapter<BottomSheetSubredditAdapter.SubredditViewHolder> {

    private List<Subreddit> mSubreddits;

    void updateData(@NonNull List<Subreddit> subreddits) {
        mSubreddits = new ArrayList<>(subreddits);
        notifyDataSetChanged();
    }

    BottomSheetSubredditAdapter() {
        mSubreddits = new ArrayList<>();
    }

    @NonNull
    @Override
    public BottomSheetSubredditAdapter.SubredditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemSubredditBinding subredditItemBinding = ItemSubredditBinding.inflate(layoutInflater, parent, false);
        return new BottomSheetSubredditAdapter.SubredditViewHolder(subredditItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetSubredditAdapter.SubredditViewHolder holder, int position) {
        holder.bind(mSubreddits.get(position));
    }

    @Override
    public int getItemCount() {
        return mSubreddits == null ? 0 : mSubreddits.size();
    }

    class SubredditViewHolder extends RecyclerView.ViewHolder {
        private final ItemSubredditBinding binding;

        SubredditViewHolder(ItemSubredditBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Subreddit subreddit) {
            binding.setSubreddit(subreddit);
            binding.executePendingBindings();
        }
    }
}
