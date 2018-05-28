package com.jollyremedy.notreddit.api;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.jollyremedy.notreddit.Constants.SharedPreferenceKeys;
import com.jollyremedy.notreddit.auth.accounting.Accountant;
import com.jollyremedy.notreddit.models.auth.Token;
import com.jollyremedy.notreddit.repository.TokenRepository;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class OAuthTokenInterceptor implements Interceptor {
    private static final String TAG = "OAuthTokenInterceptor";
    private SharedPreferences mSharedPreferences;
    private TokenRepository mTokenRepository;

    @Inject
    public OAuthTokenInterceptor(SharedPreferences sharedPreferences, TokenRepository tokenRepository) {
        mSharedPreferences = sharedPreferences;
        mTokenRepository = tokenRepository;
    }

    @Override
    public Response intercept(final @NonNull Chain chain) throws IOException {
        String tempUserToken = mSharedPreferences.getString(SharedPreferenceKeys.TEMP_USER_TOKEN, null);
        if (tempUserToken != null) {
            Log.d(TAG, "Temporary user token exists. Immediately performing request with this token.");
            return chain.proceed(requestWithToken(chain.request(), tempUserToken));
        }

        String currentUsername = mSharedPreferences.getString(SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN, null);
        Log.d(TAG, "Current username: " + currentUsername);

        if (currentUsername != null) {
            Log.wtf(TAG, "Performing authenticated call for current user...");
            Response userAuthenticatedResponse = performAuthenticatedCallForCurrentUser(chain);
            if (userAuthenticatedResponse != null) {
                return userAuthenticatedResponse;
            }
        } else {
            Log.wtf(TAG, "Performing authenticated call app (no current user)...");
            Response appAuthenticatedResponse = performAuthenticatedCallForApp(chain);
            if (appAuthenticatedResponse != null) {
                return appAuthenticatedResponse;
            }
        }

        throw new IOException("Couldn't perform OAuth token intercept.");
    }

    @Nullable
    private Response performAuthenticatedCallForCurrentUser(Chain chain) throws IOException {
        String currentAccountToken = Accountant.getInstance().getCurrentAccessToken();
        Log.wtf(TAG, "Current account token: " + currentAccountToken);

        String currentRefreshToken = Accountant.getInstance().getCurrentRefreshToken();
        Log.wtf(TAG, "Current refresh token: " + currentRefreshToken);

        if (currentAccountToken == null || currentRefreshToken == null) {
            return null;
        }

        Log.wtf(TAG, "Since we have an account token, perform the request with it attached...");
        Response initialResult = chain.proceed(requestWithToken(chain.request(), currentAccountToken));

        if (responseIsNotAuthFailure(initialResult)) {
            Log.wtf(TAG, "This initial request was a success! Or, at least didn't fail due to authorization.");
            return initialResult;
        } else {
            Log.wtf(TAG, "This initial request failed due to a 401 or 403.");
        }

        Token refreshedToken = mTokenRepository.getRefreshedUserToken(currentRefreshToken);
        Log.wtf(TAG, "Refreshed token: " + new Gson().toJson(refreshedToken));

        if (refreshedToken != null) {
            Log.wtf(TAG, "Since we now have a new token from the refresh token, perform the initial request again...");

            Response resultFromRequestWithRefreshedToken = chain.proceed(requestWithToken(chain.request(), refreshedToken.getAccessToken()));
            if (responseIsNotAuthFailure(resultFromRequestWithRefreshedToken)) {
                Accountant.getInstance().updateCurrentAccessToken(refreshedToken.getAccessToken());
                Log.wtf(TAG, "This request was finally a success! Or, at least didn't fail due to authorization.");
                return resultFromRequestWithRefreshedToken;
            } else {
                Log.wtf(TAG, "This final request with a refreshed auth-token failed due to a 401 or 403.");
                throw new IOException("Unable to perform an authenticated request for the current user.");
            }
        }

        return null;
    }

    @Nullable
    private Response performAuthenticatedCallForApp(Chain chain) throws IOException {
        String currentAppToken = mSharedPreferences.getString(SharedPreferenceKeys.APPLICATION_TOKEN, null);
        Log.wtf(TAG, "Current application token: " + currentAppToken);

        if (currentAppToken != null) {
            Log.wtf(TAG, "Have application token, performing request...");
            Response appAuthenticatedResponse = chain.proceed(requestWithToken(chain.request(), currentAppToken));

            if (responseIsNotAuthFailure(appAuthenticatedResponse)) {
                return appAuthenticatedResponse;
            }

            try {
                Log.d(TAG, "Application-authenticated request failed, getting new app token...");
                Token appToken = mTokenRepository.getAppToken(mSharedPreferences.getString(SharedPreferenceKeys.DEVICE_ID, null));

                Log.d(TAG, "Got new app token, trying again: " + appToken);
                Response appRetryAuthenticatedResponse = chain.proceed(requestWithToken(chain.request(), currentAppToken));

                if (appRetryAuthenticatedResponse != null) {
                    mSharedPreferences.edit()
                            .putString(SharedPreferenceKeys.APPLICATION_TOKEN, appToken.getAccessToken())
                            .apply();
                    Log.d(TAG, "Second request was successful!");
                    return appRetryAuthenticatedResponse;
                }
            } catch (RuntimeException e) {
                Log.e(TAG, "Error fetching token.", e);
            }

        } else {
            try {
                Token appToken = mTokenRepository.getAppToken(mSharedPreferences.getString(SharedPreferenceKeys.DEVICE_ID, null));
                return chain.proceed(requestWithToken(chain.request(), appToken.getAccessToken()));
            } catch (RuntimeException e) {
                Log.e(TAG, "Error fetching token.", e);
            }
        }

        return null;
    }

    private Request requestWithToken(Request request, String token) {
        return request.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    private boolean responseIsNotAuthFailure(Response response) {
        return response.code() != HTTP_FORBIDDEN && response.code() != HTTP_UNAUTHORIZED;
    }
}