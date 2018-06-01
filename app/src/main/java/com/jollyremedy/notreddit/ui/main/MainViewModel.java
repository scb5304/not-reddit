package com.jollyremedy.notreddit.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.gson.Gson;
import com.jollyremedy.notreddit.auth.accounting.Accountant;
import com.jollyremedy.notreddit.models.subreddit.SubredditForUserWhere;
import com.jollyremedy.notreddit.models.subreddit.SubredditListing;
import com.jollyremedy.notreddit.models.subreddit.SubredditWhere;
import com.jollyremedy.notreddit.repository.SubredditRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

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
            fetchSubreddits();
        }
        return mListingLiveData;
    }

    private void fetchSubreddits() {
        Single<SubredditListing> fetchSingle;

        if (Accountant.getInstance().getCurrentAccessToken() != null) {
            List<String> subredditWheres = Collections.singletonList(SubredditWhere.DEFAULT);
            List<String> subredditForUserWheres = Collections.singletonList(SubredditForUserWhere.SUBSCRIBER);
            fetchSingle = mSubredditRepository.getSubredditsForParams(subredditWheres, subredditForUserWheres);
        } else {
            fetchSingle = mSubredditRepository.getSubredditsWhere(SubredditWhere.DEFAULT);
        }

        fetchSingle.subscribe(this::onSubredditListingReceived, this::onSubredditListingFetchError);
    }

    private void onSubredditListingReceived(SubredditListing subredditListing) {
        Log.i(TAG, "Got a subreddit listing. " + new Gson().toJson(subredditListing));
        mListingLiveData.postValue(subredditListing);
    }

    private void onSubredditListingFetchError(Throwable t) {
        Log.e(TAG, "Failed to get a post listing!", t);
    }

    public void onLoggedIn() {
        fetchSubreddits();
    }

    public void onLoggedOut() {
        fetchSubreddits();
    }
}
