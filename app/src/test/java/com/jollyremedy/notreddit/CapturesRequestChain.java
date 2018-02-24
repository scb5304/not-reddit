package com.jollyremedy.notreddit;

import android.support.annotation.NonNull;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static org.mockito.Mockito.mock;

/**
 * A stubbed OkHttp3 {@link okhttp3.Interceptor.Chain} that provides an initial Request with the
 * passed URL when {@link Interceptor.Chain#request()} is called. It then stores the Request that
 * was passed to {@link okhttp3.Interceptor.Chain#proceed(Request)}. Allows for assertions to be made
 * on the chained Request.
 */
public class CapturesRequestChain implements Interceptor.Chain {

    private String mUrl;
    private Request mCapturedRequest;

    public CapturesRequestChain(String url) {
        mUrl = url;
    }

    @Override
    public Request request() {
        return new Request.Builder()
                .url(mUrl)
                .build();
    }

    @Override
    public Response proceed(@NonNull Request request) throws IOException {
        mCapturedRequest = request;
        return mock(Response.class);
    }

    @Nullable
    @Override
    public Connection connection() {
        return mock(Connection.class);
    }

    public Request getCapturedRequest() {
        return mCapturedRequest;
    }
}
