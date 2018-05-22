package com.jollyremedy.notreddit.api;

import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jollyremedy.notreddit.Constants;
import com.jollyremedy.notreddit.Constants.SharedPreferenceKeys;
import com.jollyremedy.notreddit.models.auth.Token;
import com.jollyremedy.notreddit.repository.TokenRepository;

import java.io.IOException;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

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
        //TODO maybe:
        //Is there a user currently logged in?
        //  If so, get the current token, and use that.
        //      If that fails due to a 403, get the current refresh token, and use that.
        //          If that ALSO fails due to a 403, invalidate the auth token, and somehow get them to re-sign in? Or show an error and tell them to remove the account.
        //If there is not a user currently logged in, is there currently an app-only token stored in shared preferences?
        //  If so, use it. If that fails, request a new one. Try with the returned one, and if that fails, remove the token and show an error.
        //  If not, request a new one, then try again. Try with the returned one, and if that fails, remove the token and show an error.
        String tokenInSharedPref = mSharedPreferences.getString(SharedPreferenceKeys.TEMP_USER_TOKEN, null);
        if (tokenInSharedPref != null) {
            Request request = requestWithToken(chain.request(), tokenInSharedPref);
            return chain.proceed(request);
        }
        return chain.proceed(chain.request());
    }

    private Response requestTokenAndAddToRequest(Chain chain) throws IOException {
        String newToken = getNewAccessToken();
        mSharedPreferences.edit()
                .putString(SharedPreferenceKeys.TOKEN, newToken)
                .apply();

        if (newToken != null) {
            Request request = requestWithToken(chain.request(), newToken);
            return chain.proceed(request);
        } else {
            return chain.proceed(chain.request());
        }
    }

    @Nullable
    private String getNewAccessToken() throws IOException {
        String accessToken = null;
        String deviceId = mSharedPreferences.getString(SharedPreferenceKeys.DEVICE_ID, null);
        Token token = mTokenRepository.getTokenSync(deviceId);

        if (token == null) {
            Log.e(TAG, "Failed to get a new token when intercepting a request.");
        } else {
            accessToken = token.getAccessToken();
        }
        return accessToken;
    }

    private Request requestWithToken(Request request, String token) {
        return request.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    private boolean requestRequiresNewToken(Response responseFromInitialRequest) {
        //noinspection SimplifiableIfStatement
        if (responseFromInitialRequest.isSuccessful()) {
            return false;
        } else {
            return responseFromInitialRequest.code() == HTTP_FORBIDDEN || responseFromInitialRequest.code() == HTTP_UNAUTHORIZED;
        }
    }
}