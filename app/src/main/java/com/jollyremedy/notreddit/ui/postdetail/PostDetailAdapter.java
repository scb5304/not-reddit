package com.jollyremedy.notreddit.ui.postdetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.databinding.ItemCommentBinding;
import com.jollyremedy.notreddit.databinding.PartialPostHeaderBinding;
import com.jollyremedy.notreddit.models.comment.Comment;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.util.Utility;

import java.util.ArrayList;
import java.util.List;

import static com.jollyremedy.notreddit.util.Utility.isEven;

public class PostDetailAdapter extends RecyclerView.Adapter {

    private static final int HEADER = 1;
    private static final int COMMENTS = 2;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private Post mPost;
    private List<Comment> mComments;
    private PostDetailBindingDataProvider mDataBindingProvider;

    PostDetailAdapter(Context context, Post post) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mComments = new ArrayList<>();
        mPost = post;
        mDataBindingProvider = new PostDetailBindingDataProvider(mContext);
    }

    public void setPost(Post post) {
        mPost = post;
    }

    public void setComments(List<Comment> comments) {
        mComments = comments;
        mDataBindingProvider.setAllComments(mComments);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        }
        return COMMENTS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                PartialPostHeaderBinding headerBinding = PartialPostHeaderBinding.inflate(mLayoutInflater, parent, false);
                return new HeaderViewHolder(headerBinding);
            default:
                ItemCommentBinding itemCommentBinding = ItemCommentBinding.inflate(mLayoutInflater, parent, false);
                return new CommentViewHolder(itemCommentBinding);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(mPost);
        } else if (holder instanceof CommentViewHolder) {
            ((CommentViewHolder) holder).bind(position);
        } else {
            throw new RuntimeException("Unknown view holder: " + holder.toString());
        }
    }

    @Override
    public int getItemCount() {
        return mComments.size() + 1;
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        private final ItemCommentBinding binding;

        CommentViewHolder(ItemCommentBinding itemCommentBinding) {
            super(itemCommentBinding.getRoot());
            this.binding = itemCommentBinding;
        }

        public void bind(int adapterPosition) {
            int commentPosition = adapterPosition - 1;
            Comment currentComment = mComments.get(commentPosition);
            fillVerticalLinesForComment(binding.itemCommentRoot, currentComment);

            mDataBindingProvider.setCurrentCommentPosition(commentPosition);
            binding.setDataProvider(mDataBindingProvider);
            binding.executePendingBindings();
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final PartialPostHeaderBinding binding;

        HeaderViewHolder(PartialPostHeaderBinding headerBinding) {
            super(headerBinding.getRoot());
            this.binding = headerBinding;
        }

        public void bind(Post post) {
            binding.setPost(post);
            binding.postDetailPostItem.setPost(post);
            binding.executePendingBindings();
        }
    }

    private void fillVerticalLinesForComment(ViewGroup commentLayout, Comment comment) {
        int offset = Utility.isEven(comment.getData().getDepth()) ? 1 : 0;

        //Vertical lines added by other view holders remain and should be removed.
        int viewsInLayoutCount = commentLayout.getChildCount();
        if (viewsInLayoutCount > 1) {
            commentLayout.removeViews(0, viewsInLayoutCount - 1);
        }
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
