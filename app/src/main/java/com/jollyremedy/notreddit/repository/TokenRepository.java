package com.jollyremedy.notreddit.repository;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jollyremedy.notreddit.BuildConfig;
import com.jollyremedy.notreddit.Constants;
import com.jollyremedy.notreddit.api.AuthConstants;
import com.jollyremedy.notreddit.api.RequestTokenApi;
import com.jollyremedy.notreddit.models.auth.Token;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class TokenRepository {

    private static final String TAG = "TokenRepository";
    private RequestTokenApi mRequestTokenApi;
    private SharedPreferences mSharedPreferences;

    @Inject
    TokenRepository(RequestTokenApi requestTokenApi, SharedPreferences sharedPreferences) {
        mRequestTokenApi = requestTokenApi;
        mSharedPreferences = sharedPreferences;
    }

    @Nullable
    public Token getAppToken(String deviceId) {
        Token token = null;
        Call<Token> tokenCall = mRequestTokenApi.getTokenAppOnlyFlow(AuthConstants.AUTH_GRANT_TYPE_INSTALLED, deviceId);
        try {
            Response<Token> tokenResponse = tokenCall.execute();
            if (tokenResponse.isSuccessful()) {
                token = tokenResponse.body();
            } else {
                Log.e(TAG, "Failed to get a token! " + tokenResponse.errorBody());
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException occurred during token fetch.", e);
        }

        return token;
    }

    @Nullable
    public Token getRefreshedUserToken(String refreshToken)  {
        String deviceId = mSharedPreferences.getString(Constants.SharedPreferenceKeys.DEVICE_ID, null);
        Token token = null;
        Call<Token> tokenCall = mRequestTokenApi.getRefreshToken(AuthConstants.AUTH_GRANT_TYPE_INSTALLED, deviceId, refreshToken);
        try {
            Response<Token> tokenResponse = tokenCall.execute();
            if (tokenResponse.isSuccessful()) {
                token = tokenResponse.body();
            } else {
                Log.e(TAG, "Failed to get a refreshed token! " + tokenResponse.errorBody());
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException occurred during refreshed token fetch.", e);
        }

        return token;
    }

    public Single<Token> getUserToken(@NonNull String authCode) {
        return mRequestTokenApi.getTokenCodeFlow(AuthConstants.AUTH_GRANT_TYPE_CODE, authCode, BuildConfig.REDIRECT_URI)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
