package com.jollyremedy.notreddit.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.jollyremedy.notreddit.Constants;
import com.jollyremedy.notreddit.models.subreddit.Subreddit;
import com.jollyremedy.notreddit.models.subreddit.SubredditListing;
import com.jollyremedy.notreddit.models.subreddit.SubredditWhere;
import com.jollyremedy.notreddit.repository.SubredditRepository;
import com.jollyremedy.notreddit.ui.common.SingleLiveEvent;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";
    private SubredditRepository mSubredditRepository;
    private SharedPreferences mSharedPreferences;
    private MutableLiveData<SubredditListing> mListingLiveData;
    private SingleLiveEvent<String> mLoginUrlLiveData;

    @Inject
    MainViewModel(SubredditRepository subredditRepository, SharedPreferences sharedPreferences) {
        mSubredditRepository = subredditRepository;
        mSharedPreferences = sharedPreferences;
        mListingLiveData = new MutableLiveData<>();
    }

    LiveData<SubredditListing> getObservableSubredditListing() {
        if (mListingLiveData.getValue() == null) {
            mSubredditRepository.getSubredditsWhere(SubredditWhere.DEFAULT, new SubredditListingObserver());
        }
        return mListingLiveData;
    }

    SingleLiveEvent<String> getObservableLoginUrl() {
        mLoginUrlLiveData = new SingleLiveEvent<>();
        return mLoginUrlLiveData;
    }

    private void onSubredditListingReceived(SubredditListing subredditListing) {
        Log.i(TAG, "Got a subreddit listing. " + new Gson().toJson(subredditListing));
        mListingLiveData.postValue(subredditListing);
    }

    private void onSubredditListingFetchError(Throwable t) {
        Log.e(TAG, "Failed to get a post listing!", t);
    }

    public void onLoginPressed() {
        String authString = mSharedPreferences.getString(Constants.SharedPreferenceKeys.DEVICE_ID, "");
        String url = "https://www.reddit.com/api/v1/authorize.compact?client_id=Fy9zcX04SkqIhw&response_type=code&state=" + authString + "&duration=permanent&redirect_uri=notreddit://callback&scope=vote mysubreddits";
        Log.d(TAG, "url: " + url);
        mLoginUrlLiveData.setValue(authString);
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
