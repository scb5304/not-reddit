package com.jollyremedy.notreddit.api;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.base.Strings;
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
            Response userAuthenticatedResponse = performAuthenticatedCallForCurrentUser(chain);
            if (userAuthenticatedResponse != null) {
                return userAuthenticatedResponse;
            }
        }

        return chain.proceed(chain.request());
    }

    @Nullable
    private Response performAuthenticatedCallForCurrentUser(Chain chain) throws IOException {
        Log.wtf(TAG, "Performing authenticated call for current user...");

        String currentAccountToken = Accountant.getInstance().getCurrentAccessToken();
        Log.wtf(TAG, "Current account token: " + currentAccountToken);

        if (currentAccountToken != null) {
            Log.wtf(TAG, "Since we have an account token, perform the request with it attached...");
            Response initialResult = chain.proceed(requestWithToken(chain.request(), currentAccountToken));

            if (!responseFailedDueToAuth(initialResult)) {
                Log.wtf(TAG, "This initial request was a success! Or, at least didn't fail due to authorization.");
                return initialResult;
            }

            Log.wtf(TAG, "This initial request failed due to a 401 or 403.");

            String currentRefreshToken = Accountant.getInstance().getCurrentRefreshToken();
            Log.wtf(TAG, "Current refresh token: " + currentRefreshToken);

            if (currentRefreshToken != null) {
                Log.wtf(TAG, "Since we have a refresh token, perform a request to get a new token...");

                Token refreshedToken = mTokenRepository.getRefreshedToken(currentRefreshToken);
                Log.wtf(TAG, "Refreshed token: " + new Gson().toJson(refreshedToken));

                if (refreshedToken != null) {
                    Log.wtf(TAG, "Since we now have a new token from the refresh token, perform the initial request again...");

                    Response resultFromRequestWithRefreshedToken = chain.proceed(requestWithToken(chain.request(), refreshedToken.getAccessToken()));
                    if (!responseFailedDueToAuth(resultFromRequestWithRefreshedToken)) {
                        Log.wtf(TAG, "This request was finally a success! Or, at least didn't fail due to authorization.");
                        return resultFromRequestWithRefreshedToken;
                    } else {
                        Log.wtf(TAG, "This final request with a refreshed auth-token failed due to a 401 or 403.");
                    }
                }
            }
        }

        Log.wtf(TAG, "Returning null!");
        return null;
    }

    private Request requestWithToken(Request request, String token) {
        return request.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    private boolean responseFailedDueToAuth(Response response) {
        return response.code() == HTTP_FORBIDDEN || response.code() == HTTP_UNAUTHORIZED;
    }
}