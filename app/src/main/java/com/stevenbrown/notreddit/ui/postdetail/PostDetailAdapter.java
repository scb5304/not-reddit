package com.stevenbrown.notreddit.ui.postdetail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Range;
import com.stevenbrown.notreddit.R;
import com.stevenbrown.notreddit.databinding.ItemCommentBinding;
import com.stevenbrown.notreddit.databinding.PartialPostHeaderBinding;
import com.stevenbrown.notreddit.models.comment.Comment;
import com.stevenbrown.notreddit.models.post.Post;
import com.stevenbrown.notreddit.util.Utility;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.stevenbrown.notreddit.util.Utility.isEven;

public class PostDetailAdapter extends RecyclerView.Adapter {

    private static final int HEADER = 1;
    private static final int COMMENTS = 2;

    private LayoutInflater mLayoutInflater;
    private Post mPost;
    private List<Comment> mComments;
    private PostDetailViewModel mViewModel;

    PostDetailAdapter(Context context, PostDetailViewModel viewModel, Post post) {
        mLayoutInflater = LayoutInflater.from(context);
        mComments = new ArrayList<>();
        mPost = post;
        mViewModel = viewModel;
    }


    public void updateData(PostDetailData postDetailData) {
        mPost = postDetailData.getPost();
        mComments = postDetailData.getComments();

        Range<Integer> changeRange = postDetailData.getCommentsChangingRange();
        Range<Integer> deleteRange = postDetailData.getCommentsDeletingRange();

        if (changeRange != null) {
            //Add one to the end. If the items from [1, 2] are changing, that's a start at 1 and a count 2 changing
            //Also, add one to the beginning index: there's a post at index 1.
            Integer numberOfItemsChanging = changeRange.upperEndpoint() - changeRange.lowerEndpoint() + 1;
            notifyItemRangeChanged(changeRange.lowerEndpoint() + 1, numberOfItemsChanging);
        }
        if (deleteRange != null) {
            Integer numberOfItemsRemoving = deleteRange.upperEndpoint() - deleteRange.lowerEndpoint() + 1;
            notifyItemRangeRemoved(deleteRange.lowerEndpoint() + 1, numberOfItemsRemoving);
        }

        if (changeRange == null && deleteRange == null){
            Timber.w("No change or delete range!");
            notifyDataSetChanged();
        }

        postDetailData.clearRanges();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        }
        return COMMENTS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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

            binding.setViewModel(mViewModel);
            binding.setComment(currentComment);
            binding.executePendingBindings();
        }
    }

    private void fillVerticalLinesForComment(ViewGroup commentLayout, Comment comment) {
        int offset = Utility.isEven(comment.getDepth()) ? 1 : 0;

        //Vertical lines added by other view holders remain and should be removed.
        int viewsInLayoutCount = commentLayout.getChildCount();
        if (viewsInLayoutCount > 1) {
            commentLayout.removeViews(0, viewsInLayoutCount - 1);
        }
        for (int i = 0; i < comment.getDepth(); i++) {
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
