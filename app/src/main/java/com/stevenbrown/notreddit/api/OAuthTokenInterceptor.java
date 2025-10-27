package com.stevenbrown.notreddit.api;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stevenbrown.notreddit.Constants.SharedPreferenceKeys;
import com.stevenbrown.notreddit.auth.accounting.Accountant;
import com.stevenbrown.notreddit.models.auth.Token;
import com.stevenbrown.notreddit.repository.TokenRepository;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

public class OAuthTokenInterceptor implements Interceptor {
    private SharedPreferences mSharedPreferences;
    private TokenRepository mTokenRepository;
    private Accountant mAccountant;

    @Inject
    public OAuthTokenInterceptor(SharedPreferences sharedPreferences,
                                 TokenRepository tokenRepository,
                                 Accountant accountant) {
        mSharedPreferences = sharedPreferences;
        mTokenRepository = tokenRepository;
    }

    @Override
    public Response intercept(final @NonNull Chain chain) throws IOException {
        String tempUserToken = mSharedPreferences.getString(SharedPreferenceKeys.TEMP_USER_TOKEN, null);
        if (tempUserToken != null) {
            return chain.proceed(requestWithToken(chain.request(), tempUserToken));
        }

        String currentUsername = mSharedPreferences.getString(SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN, null);
        if (currentUsername != null) {
            Response userAuthenticatedResponse = performAuthenticatedCallForCurrentUser(chain);
            if (userAuthenticatedResponse != null) {
                return userAuthenticatedResponse;
            }
        } else {
            Response appAuthenticatedResponse = performAuthenticatedCallForApp(chain);
            if (appAuthenticatedResponse != null) {
                return appAuthenticatedResponse;
            }
        }

        throw new IOException("Couldn't perform OAuth token intercept.");
    }

    @Nullable
    private Response performAuthenticatedCallForCurrentUser(Chain chain) throws IOException {
        String currentAccountToken = mAccountant.getCurrentAccessToken();
        String currentRefreshToken = mAccountant.getCurrentRefreshToken();

        if (currentAccountToken == null || currentRefreshToken == null) {
            return null;
        }

        Response initialResult = chain.proceed(requestWithToken(chain.request(), currentAccountToken));
        if (!responseIsAuthFailure(initialResult)) {
            return initialResult;
        }

        Token refreshedToken = mTokenRepository.getRefreshedUserToken(currentRefreshToken);

        if (refreshedToken != null) {
            Response resultFromRequestWithRefreshedToken = chain.proceed(requestWithToken(chain.request(), refreshedToken.getAccessToken()));
            if (!responseIsAuthFailure(resultFromRequestWithRefreshedToken)) {
                mAccountant.updateCurrentAccessToken(refreshedToken.getAccessToken());
                return resultFromRequestWithRefreshedToken;
            }
        } else {
            Timber.e("No refresh token to use!");
        }

        return null;
    }

    @Nullable
    private Response performAuthenticatedCallForApp(Chain chain) throws IOException {
        String currentAppToken = mSharedPreferences.getString(SharedPreferenceKeys.APPLICATION_TOKEN, null);

        if (currentAppToken != null) {
            Response appAuthenticatedResponse = chain.proceed(requestWithToken(chain.request(), currentAppToken));
            if (responseIsAuthFailure(appAuthenticatedResponse)) {
                mSharedPreferences.edit()
                        .putString(SharedPreferenceKeys.APPLICATION_TOKEN, null)
                        .apply();
            }
            return appAuthenticatedResponse;
        }

        try {
            Token appToken = mTokenRepository.getAppToken(mSharedPreferences.getString(SharedPreferenceKeys.DEVICE_ID, null));
            Response appRetryAuthenticatedResponse = chain.proceed(requestWithToken(chain.request(), appToken.getAccessToken()));

            if (!responseIsAuthFailure(appRetryAuthenticatedResponse)) {
                mSharedPreferences.edit()
                        .putString(SharedPreferenceKeys.APPLICATION_TOKEN, appToken.getAccessToken())
                        .apply();
                return appRetryAuthenticatedResponse;
            }
        } catch (RuntimeException e) {
            Timber.e(e, "Error fetching token.");
        }

        return null;
    }

    private Request requestWithToken(Request request, String token) {
        return request.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    private boolean responseIsAuthFailure(Response response) {
        return response.code() == HTTP_UNAUTHORIZED;
    }
}