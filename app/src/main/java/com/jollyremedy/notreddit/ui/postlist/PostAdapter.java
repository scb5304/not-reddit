package com.jollyremedy.notreddit.ui.postlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
    private Context mContext;

    void updateData(List<Post> posts) {
        mPosts = posts;
        notifyDataSetChanged();
    }

    PostAdapter(Context context) {
        mContext = context;
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
        PostData postData = mPosts.get(position).getData();
        holder.bind(postData);
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

        public void bind(PostData postData) {
            binding.setPostData(postData);
            binding.executePendingBindings();
        }
    }
}
