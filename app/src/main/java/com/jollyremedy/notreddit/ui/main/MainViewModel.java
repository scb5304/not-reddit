package com.jollyremedy.notreddit.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.jollyremedy.notreddit.models.subreddit.Subreddit;
import com.jollyremedy.notreddit.models.subreddit.SubredditListing;
import com.jollyremedy.notreddit.models.subreddit.SubredditWhere;
import com.jollyremedy.notreddit.repository.SubredditRepository;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";
    private SubredditRepository mSubredditRepository;
    private MutableLiveData<SubredditListing> mListingLiveData;

    @Inject
    MainViewModel(SubredditRepository subredditRepository) {
        mSubredditRepository = subredditRepository;
        mListingLiveData = new MutableLiveData<>();
    }

    LiveData<SubredditListing> getObservableSubredditListing() {
        if (mListingLiveData.getValue() == null) {
            mSubredditRepository.getSubredditsWhere(SubredditWhere.DEFAULT, new SubredditListingObserver());
        }
        return mListingLiveData;
    }

    private void onSubredditListingReceived(SubredditListing subredditListing) {
        Log.i(TAG, "Got a subreddit listing. " + new Gson().toJson(subredditListing));
        mListingLiveData.postValue(subredditListing);
    }

    private void onSubredditListingFetchError(Throwable t) {
        Log.e(TAG, "Failed to get a post listing!", t);
    }

    private class SubredditListingObserver implements SingleObserver<SubredditListing> {

        @Override
        public void onSubscribe(Disposable d) {
            Log.i(TAG, "Getting subreddits...");
        }

        @Override
        public void onSuccess(SubredditListing subredditListing) {
            onSubredditListingReceived(subredditListing);
        }

        @Override
        public void onError(Throwable t) {
            onSubredditListingFetchError(t);
        }
    }

}
