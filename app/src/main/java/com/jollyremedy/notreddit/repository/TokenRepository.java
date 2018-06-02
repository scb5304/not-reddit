package com.jollyremedy.notreddit.repository;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jollyremedy.notreddit.BuildConfig;
import com.jollyremedy.notreddit.Constants;
import com.jollyremedy.notreddit.api.AuthConstants;
import com.jollyremedy.notreddit.api.RequestTokenApi;
import com.jollyremedy.notreddit.models.auth.Token;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TokenRepository {

    private RequestTokenApi mRequestTokenApi;
    private SharedPreferences mSharedPreferences;

    @Inject
    TokenRepository(RequestTokenApi requestTokenApi, SharedPreferences sharedPreferences) {
        mRequestTokenApi = requestTokenApi;
        mSharedPreferences = sharedPreferences;
    }

    /**
     * Retrieves an application (not user) token from the Reddit API in a blocking fashion. Throws an
     * exception if an error occurs.
     */
    @NonNull
    public Token getAppToken(String deviceId) throws RuntimeException {
        return mRequestTokenApi.getTokenAppOnlyFlow(AuthConstants.AUTH_GRANT_TYPE_INSTALLED, deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .blockingGet();
    }

    /**
     * Retrieves a new token from the Reddit API using the passed refresh token, and the device ID
     * stored in shared preferences. Performed in a blocking fashion. Throws an exception if an error
     * occurs.
     */
    @Nullable
    public Token getRefreshedUserToken(String refreshToken) throws RuntimeException {
        String deviceId = mSharedPreferences.getString(Constants.SharedPreferenceKeys.DEVICE_ID, null);
        return mRequestTokenApi.getRefreshedToken(AuthConstants.AUTH_GRANT_TYPE_INSTALLED, deviceId, refreshToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .blockingGet();

    }

    /**
     * Retrieves a user token from the Reddit API in an asynchronous fashion given the auth code
     * contained within the redirect_uri from a reddit login.
     */
    @NonNull
    public Single<Token> getUserToken(@NonNull String authCode) {
        return mRequestTokenApi.getTokenCodeFlow(AuthConstants.AUTH_GRANT_TYPE_CODE, authCode, BuildConfig.REDIRECT_URI)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
