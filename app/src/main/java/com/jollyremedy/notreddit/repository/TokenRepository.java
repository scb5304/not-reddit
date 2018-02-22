package com.jollyremedy.notreddit.repository;

import android.support.annotation.Nullable;
import android.util.Log;

import com.jollyremedy.notreddit.api.AuthConstants;
import com.jollyremedy.notreddit.api.OAuthTokenInterceptor;
import com.jollyremedy.notreddit.api.RequestTokenApi;
import com.jollyremedy.notreddit.models.auth.Token;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class TokenRepository {

    private static final String TAG = "TokenRepository";
    private RequestTokenApi mRequestTokenApi;

    @Inject
    TokenRepository(RequestTokenApi requestTokenApi) {
        mRequestTokenApi = requestTokenApi;
    }

    @Nullable
    public Token getTokenSync(String deviceId) {
        Token token = null;
        Call<Token> tokenCall = mRequestTokenApi.getToken(AuthConstants.AUTH_GRANT_TYPE, deviceId);
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
}
