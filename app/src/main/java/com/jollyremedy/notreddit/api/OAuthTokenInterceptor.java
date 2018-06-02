package com.jollyremedy.notreddit.api;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jollyremedy.notreddit.Constants.SharedPreferenceKeys;
import com.jollyremedy.notreddit.auth.accounting.Accountant;
import com.jollyremedy.notreddit.models.auth.Token;
import com.jollyremedy.notreddit.repository.TokenRepository;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class OAuthTokenInterceptor implements Interceptor {
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
            Timber.d("Temporary user token exists. Immediately performing request with this token.");
            return chain.proceed(requestWithToken(chain.request(), tempUserToken));
        }

        String currentUsername = mSharedPreferences.getString(SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN, null);
        Timber.d("Current username: %s", currentUsername);

        if (currentUsername != null) {
            Timber.d("Performing authenticated call for current user...");
            Response userAuthenticatedResponse = performAuthenticatedCallForCurrentUser(chain);
            if (userAuthenticatedResponse != null) {
                return userAuthenticatedResponse;
            }
        } else {
            Timber.d("Performing authenticated call app (no current user)...");
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
        Timber.d("Current account token: %s", currentAccountToken);

        String currentRefreshToken = Accountant.getInstance().getCurrentRefreshToken();
        Timber.d("Current refresh token: %s", currentRefreshToken);

        if (currentAccountToken == null || currentRefreshToken == null) {
            Timber.e("No current account token or refresh token!");
            return null;
        }

        Response initialResult = chain.proceed(requestWithToken(chain.request(), currentAccountToken));
        if (responseIsNotAuthFailure(initialResult)) {
            return initialResult;
        }

        Timber.w("This initial request failed due to a 401 or 403.");
        Token refreshedToken = mTokenRepository.getRefreshedUserToken(currentRefreshToken);

        if (refreshedToken != null) {
            Response resultFromRequestWithRefreshedToken = chain.proceed(requestWithToken(chain.request(), refreshedToken.getAccessToken()));
            if (responseIsNotAuthFailure(resultFromRequestWithRefreshedToken)) {
                Accountant.getInstance().updateCurrentAccessToken(refreshedToken.getAccessToken());
                Timber.d("This request was finally a success!");
                return resultFromRequestWithRefreshedToken;
            } else {
                Timber.e("This final request with a refreshed auth-token failed due to a 401 or 403.");
            }
        } else {
            Timber.e("No refresh token to use!");
        }

        return null;
    }

    @Nullable
    private Response performAuthenticatedCallForApp(Chain chain) throws IOException {
        String currentAppToken = mSharedPreferences.getString(SharedPreferenceKeys.APPLICATION_TOKEN, null);
        Timber.d("Current application token: %s", currentAppToken);

        if (currentAppToken != null) {
            Timber.d("Have application token, performing request...");
            Response appAuthenticatedResponse = chain.proceed(requestWithToken(chain.request(), currentAppToken));

            if (responseIsNotAuthFailure(appAuthenticatedResponse)) {
                return appAuthenticatedResponse;
            }

            try {
                Timber.d("Application-authenticated request failed, getting new app token...");
                Token appToken = mTokenRepository.getAppToken(mSharedPreferences.getString(SharedPreferenceKeys.DEVICE_ID, null));

                Timber.d("Got new app token, trying again: %s", appToken);
                Response appRetryAuthenticatedResponse = chain.proceed(requestWithToken(chain.request(), appToken.getAccessToken()));

                if (responseIsNotAuthFailure(appRetryAuthenticatedResponse)) {
                    mSharedPreferences.edit()
                            .putString(SharedPreferenceKeys.APPLICATION_TOKEN, appToken.getAccessToken())
                            .apply();
                    Timber.d("Second request was successful!");
                    return appRetryAuthenticatedResponse;
                }
            } catch (RuntimeException e) {
                Timber.e(e, "Error fetching token.");
            }

        } else {
            try {
                Token appToken = mTokenRepository.getAppToken(mSharedPreferences.getString(SharedPreferenceKeys.DEVICE_ID, null));
                Response response = chain.proceed(requestWithToken(chain.request(), appToken.getAccessToken()));
                if (responseIsNotAuthFailure(response)) {
                    mSharedPreferences.edit()
                            .putString(SharedPreferenceKeys.APPLICATION_TOKEN, appToken.getAccessToken())
                            .apply();
                    return response;
                }
            } catch (RuntimeException e) {
                Timber.e(e, "Error fetching token.");
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