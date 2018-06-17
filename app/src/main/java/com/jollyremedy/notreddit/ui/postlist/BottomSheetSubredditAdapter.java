package com.jollyremedy.notreddit.ui.postlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jollyremedy.notreddit.databinding.ItemSubredditBinding;
import com.jollyremedy.notreddit.databinding.PartialSubredditHeaderBinding;
import com.jollyremedy.notreddit.models.subreddit.Subreddit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BottomSheetSubredditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER = 1;
    private static final int SUBREDDITS = 2;

    private LayoutInflater mLayoutInflater;
    private PostListViewModel mViewModel;
    private List<Subreddit> mSubreddits;

    void updateData(@NonNull List<Subreddit> subreddits) {
        mSubreddits = new ArrayList<>(subreddits);
        notifyDataSetChanged();
    }

    BottomSheetSubredditAdapter(Context context, PostListViewModel postListViewModel) {
        mLayoutInflater = LayoutInflater.from(context);
        mViewModel = postListViewModel;
        mSubreddits = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        }
        return SUBREDDITS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                PartialSubredditHeaderBinding headerBinding = PartialSubredditHeaderBinding.inflate(mLayoutInflater, parent, false);
                headerBinding.subredditGotoEdittext.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        Toast.makeText(v.getContext(), "You want subreddit " + v.getText().toString(), Toast.LENGTH_SHORT).show();
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        Objects.requireNonNull(imm).hideSoftInputFromWindow(v.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                });
                return new HeaderViewHolder(headerBinding);
            default:
                ItemSubredditBinding subredditItemBinding = ItemSubredditBinding.inflate(mLayoutInflater, parent, false);
                return new BottomSheetSubredditAdapter.SubredditViewHolder(subredditItemBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SubredditViewHolder) {
            ((SubredditViewHolder) holder).bind(mSubreddits.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mSubreddits == null ? 0 : mSubreddits.size();
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(PartialSubredditHeaderBinding binding) {
            super(binding.getRoot());
        }
    }

    class SubredditViewHolder extends RecyclerView.ViewHolder {
        private final ItemSubredditBinding binding;

        SubredditViewHolder(ItemSubredditBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Subreddit subreddit) {
            binding.setSubreddit(subreddit);
            binding.setPostListViewModel(mViewModel);
            binding.executePendingBindings();
        }
    }
}
