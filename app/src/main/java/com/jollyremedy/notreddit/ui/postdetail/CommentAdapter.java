package com.jollyremedy.notreddit.ui.postdetail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jollyremedy.notreddit.databinding.ItemCommentBinding;
import com.jollyremedy.notreddit.databinding.ItemPostBinding;
import com.jollyremedy.notreddit.models.comment.Comment;
import com.jollyremedy.notreddit.models.comment.CommentListing;
import com.jollyremedy.notreddit.models.parent.RedditType;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.util.NotRedditViewUtils;

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
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.itemCommentParent.getLayoutParams();
            float marginLeft = NotRedditViewUtils.convertDpToPixel(comment.getData().getDepth() * 12);
            layoutParams.setMargins((int) marginLeft, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);

            if (comment.getKind() == RedditType.Kind.MORE) {
                binding.itemCommentCard.setVisibility(View.GONE);
                binding.itemCommentMoreTextView.setVisibility(View.VISIBLE);
            } else {
                binding.itemCommentCard.setVisibility(View.VISIBLE);
                binding.itemCommentMoreTextView.setVisibility(View.GONE);

            }
            binding.executePendingBindings();
        }
    }
}
