package com.jollyremedy.notreddit.ui.postlist.postdetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.jollyremedy.notreddit.models.comment.CommentListing;
import com.jollyremedy.notreddit.repository.CommentRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class PostDetailViewModel extends ViewModel {
    private static final String TAG = "PostListViewModel";
    private CommentRepository mCommentRepository;
    private MutableLiveData<List<CommentListing>> mListingLiveData;

    private String mSubredditName;
    private String mPostId;

    @Inject
    PostDetailViewModel(CommentRepository commentRepository) {
        mCommentRepository = commentRepository;
        mListingLiveData = new MutableLiveData<>();
    }

    LiveData<List<CommentListing>> getObservableCommentTree(String subredditName, String postId) {
        mSubredditName = subredditName;
        mPostId = postId;
        mCommentRepository.getComments(new CommentTreeFetchObserver(), mSubredditName, mPostId);
        return mListingLiveData;
    }

    private class CommentTreeFetchObserver implements SingleObserver<List<CommentListing>> {

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onSuccess(List<CommentListing> commentTree) {
            mListingLiveData.postValue(commentTree);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "Oh no.", e);
        }
    }
}
