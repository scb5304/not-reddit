package com.jollyremedy.notreddit.api;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jollyremedy.notreddit.Constants;
import com.jollyremedy.notreddit.models.auth.Token;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

public class OAuthTokenInterceptor implements Interceptor {
    private static final String TAG = "OAuthTokenInterceptor";
    private SharedPreferences mSharedPreferences;
    private RequestTokenApi mRequestTokenApi;

    @Inject
    public OAuthTokenInterceptor(SharedPreferences sharedPreferences,
                                 RequestTokenApi requestTokenApi) {
        mSharedPreferences = sharedPreferences;
        mRequestTokenApi = requestTokenApi;
    }

    @Override
    public Response intercept(final @NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String tokenInSharedPref = mSharedPreferences.getString(Constants.SharedPreferenceKeys.TOKEN, null);
        if (tokenInSharedPref != null) {
            request = request.newBuilder()
                    .addHeader("Authorization", "Bearer " + tokenInSharedPref)
                    .build();
            Response response = chain.proceed(request);
            if (!response.isSuccessful() && (response.code() == 403 || response.code() == 401)) {
                Log.w(TAG, "This request failed due to a failed token. Going to get one and try again...");
                return requestTokenAndAddToRequest(chain);
            } else {
                return response;
            }
        } else {
            Log.i(TAG, "No token in SharedPreferences, fetching one first...");
            return requestTokenAndAddToRequest(chain);
        }
    }

    private Response requestTokenAndAddToRequest(Chain chain) throws IOException {
        String newToken = getNewToken();

        if (newToken != null) {
            mSharedPreferences.edit()
                    .putString(Constants.SharedPreferenceKeys.TOKEN, newToken)
                    .apply();
            Log.i(TAG, "Got a token! " + newToken);
            Request request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + newToken)
                    .build();
            return chain.proceed(request);
        } else {
            return chain.proceed(chain.request());
        }
    }

    @Nullable
    private String getNewToken() throws IOException {
        Call<Token> tokenCall = mRequestTokenApi.getToken(AuthConstants.AUTH_GRANT_TYPE, mSharedPreferences.getString("device_id", ""));
        retrofit2.Response<Token> tokenResponse = tokenCall.execute();
        String accessToken = null;

        if (tokenResponse.isSuccessful()) {
            Token token = tokenResponse.body();
            if (token != null) {
                accessToken = token.getAccessToken();
            }
        } else {
            Log.e(TAG, "Failed to get a token! " + tokenResponse.errorBody());
        }

        return accessToken;
    }
}