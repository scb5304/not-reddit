package com.jollyremedy.notreddit.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.jollyremedy.notreddit.BuildConfig;
import com.jollyremedy.notreddit.Constants;
import com.jollyremedy.notreddit.models.subreddit.SubredditForUserWhere;
import com.jollyremedy.notreddit.models.subreddit.SubredditListing;
import com.jollyremedy.notreddit.models.subreddit.SubredditWhere;
import com.jollyremedy.notreddit.repository.SubredditRepository;
import com.jollyremedy.notreddit.repository.TokenRepository;
import com.jollyremedy.notreddit.ui.common.SingleLiveEvent;
import com.jollyremedy.notreddit.util.LoginResultParser;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";
    private SubredditRepository mSubredditRepository;
    private TokenRepository mTokenRepository;
    private SharedPreferences mSharedPreferences;
    private MutableLiveData<SubredditListing> mListingLiveData;
    private SingleLiveEvent<String> mLoginUrlLiveData;

    @Inject
    MainViewModel(SubredditRepository subredditRepository,
                  TokenRepository tokenRepository,
                  SharedPreferences sharedPreferences) {
        mSubredditRepository = subredditRepository;
        mTokenRepository = tokenRepository;
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
        String deviceId = mSharedPreferences.getString(Constants.SharedPreferenceKeys.DEVICE_ID, "");
        String url = buildRedditLoginUrl(deviceId);
        Log.d(TAG, "url: " + url);
        mLoginUrlLiveData.setValue(url);
    }

    private String buildRedditLoginUrl(String state) {
        return "https://www.reddit.com/api/v1/authorize.compact" +
                "?client_id=" + BuildConfig.CLIENT_ID +
                "&response_type=" + "code" +
                "&state=" + state +
                "&duration=" + "permanent" +
                "&redirect_uri=" + BuildConfig.REDIRECT_URI +
                "&scope=" + "vote mysubreddits";
    }

    public void onLoginCallback(String uriString) {
        String deviceId = mSharedPreferences.getString(Constants.SharedPreferenceKeys.DEVICE_ID, "");
        LoginResultParser loginResultParser = new LoginResultParser();

        if (uriString == null) {
            Log.e(TAG, "Uh oh!! Redirect URI string is null.");
            return;
        }

        if (!deviceId.equals(loginResultParser.getState(uriString))) {
            Log.e(TAG, "Uh oh! They didn't pass back the device ID we sent up as 'state'.");
            return;
        }

        if (loginResultParser.isAccessDenied(uriString)) {
            Log.e(TAG, "They turned us down!");
        } else {
            Log.e(TAG, "Okay, everything seems fine: " + uriString);
            mTokenRepository.getToken(loginResultParser.getCode(uriString))
                    .subscribe(token -> {
                        Log.wtf(TAG, "Got it! " + new Gson().toJson(token));
                        mSharedPreferences.edit().putString(Constants.SharedPreferenceKeys.TOKEN, token.getAccessToken()).apply();
                        tryIt();
                    }, throwable -> {
                        Log.wtf(TAG, "Uh oh!!!", throwable);
                    });
        }
    }

    private void tryIt() {
        mSubredditRepository.getSubredditsForUserWhere(SubredditForUserWhere.SUBSCRIBER, new SubredditListingObserver());
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
