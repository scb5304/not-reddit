package com.jollyremedy.notreddit.ui.postdetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.view.View;

import com.jollyremedy.notreddit.models.comment.PostWithCommentListing;
import com.jollyremedy.notreddit.models.post.Post;
import com.jollyremedy.notreddit.repository.CommentRepository;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class PostDetailViewModel extends ViewModel {
    private static final String TAG = "PostDetailViewModel";
    private CommentRepository mCommentRepository;
    private MutableLiveData<PostWithCommentListing> mPostWithCommentsLiveData;

    @Inject
    PostDetailViewModel(CommentRepository commentRepository) {
        mCommentRepository = commentRepository;
        mPostWithCommentsLiveData = new MutableLiveData<>();
    }

    LiveData<PostWithCommentListing> getObservablePostWithComments(String postId) {
        mCommentRepository.getComments(new PostWithCommentsObserver(), postId);
        return mPostWithCommentsLiveData;
    }

    private class PostWithCommentsObserver implements SingleObserver<PostWithCommentListing> {

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onSuccess(PostWithCommentListing postWithCommentListing) {
            mPostWithCommentsLiveData.postValue(postWithCommentListing);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Oh no.", e);
        }
    }

    public boolean shouldShowPostCard(Post post) {
        return !post.getData().getSelfText().trim().isEmpty();
    }
}
