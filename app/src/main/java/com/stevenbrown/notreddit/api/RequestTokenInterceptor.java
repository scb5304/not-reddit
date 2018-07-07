package com.stevenbrown.notreddit.api;

import android.support.annotation.NonNull;

import com.stevenbrown.notreddit.BuildConfig;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * When the {@link OAuthTokenInterceptor} fetches a token, this interceptor will add the required
 * Basic Auth credentials to the header.
 */
public class RequestTokenInterceptor implements Interceptor {
    private String credentials;

    public RequestTokenInterceptor() {
        this.credentials = Credentials.basic(BuildConfig.CLIENT_ID, "");
    }

    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", credentials).build();
        return chain.proceed(authenticatedRequest);
    }
}
