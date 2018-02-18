package com.jollyremedy.notreddit.ui.postdetail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jollyremedy.notreddit.databinding.ItemCommentBinding;
import com.jollyremedy.notreddit.databinding.ItemPostBinding;
import com.jollyremedy.notreddit.models.comment.Comment;
import com.jollyremedy.notreddit.models.comment.CommentListing;
import com.jollyremedy.notreddit.models.post.Post;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> mComments;

    CommentAdapter() {
        mComments = new ArrayList<>();
    }

    void updateData(List<Comment> comments) {
        mComments = comments;
        notifyDataSetChanged();
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemCommentBinding itemCommentBinding = ItemCommentBinding.inflate(layoutInflater, parent, false);
        return new CommentViewHolder(itemCommentBinding);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        holder.bind(mComments.get(position));
    }

    @Override
    public int getItemCount() {
        return mComments == null ? 0 : mComments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        private final ItemCommentBinding binding;

        public CommentViewHolder(ItemCommentBinding itemCommentBinding) {
            super(itemCommentBinding.getRoot());
            this.binding = itemCommentBinding;
        }

        public void bind(Comment comment) {
            binding.setComment(comment);
            binding.executePendingBindings();
        }
    }
}
