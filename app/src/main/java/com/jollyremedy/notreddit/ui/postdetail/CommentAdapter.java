package com.jollyremedy.notreddit.ui.postdetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.databinding.ItemCommentBinding;
import com.jollyremedy.notreddit.models.comment.Comment;
import com.jollyremedy.notreddit.models.parent.RedditType;
import com.jollyremedy.notreddit.util.Utility;

import java.util.ArrayList;
import java.util.List;

import static com.jollyremedy.notreddit.util.Utility.isEven;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> mComments;
    private LayoutInflater mLayoutInflater;

    CommentAdapter() {
        mComments = new ArrayList<>();
    }

    void updateData(Context context, List<Comment> comments) {
        mComments = comments;
        mLayoutInflater = LayoutInflater.from(context);
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
            fillVerticalLinesForComment(binding.itemCommentParent, comment);

            if (comment.getKind() == RedditType.Kind.MORE) {
                binding.itemCommentActualContent.setVisibility(View.GONE);
                binding.itemCommentMoreWrapper.setVisibility(View.VISIBLE);
            } else {
                binding.itemCommentActualContent.setVisibility(View.VISIBLE);
                binding.itemCommentMoreWrapper.setVisibility(View.GONE);
            }

            binding.executePendingBindings();
        }
    }

    private void fillVerticalLinesForComment(LinearLayout commentLayout, Comment comment) {
        int offset = Utility.isEven(comment.getData().getDepth()) ? 1 : 0;

        for (int i = 0; i < comment.getData().getDepth(); i++) {
            View verticalLine;
            if (isEven(offset + i)) {
                verticalLine = mLayoutInflater.inflate(R.layout.comment_vertical_divider_gray, commentLayout, false);
            } else {
                verticalLine = mLayoutInflater.inflate(R.layout.comment_vertical_divider_accent, commentLayout, false);
            }
            commentLayout.addView(verticalLine, 0);
        }
    }
}
