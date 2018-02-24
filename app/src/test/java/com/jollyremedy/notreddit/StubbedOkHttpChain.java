package com.jollyremedy.notreddit;

import android.support.annotation.NonNull;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import static org.mockito.Mockito.mock;

/**
 * A stubbed OkHttp3 {@link okhttp3.Interceptor.Chain} that allows assertions on constructed requests.
 */
public class StubbedOkHttpChain implements Interceptor.Chain {

    private int mResponseCode;
    private Request mInitialRequest;
    private Request mCapturedRequest;

    /**
     * Creates a new StubbedOkHttpChain using the passed url.
     * @param url When getting the request from this chain, it will have this URL.
     */
    public StubbedOkHttpChain(String url) {
        mInitialRequest = new Request.Builder()
                .url(url)
                .build();
        mResponseCode = HttpsURLConnection.HTTP_OK;
    }

    @Override
    public Request request() {
        return mCapturedRequest == null ? mInitialRequest : mCapturedRequest;
    }

    @Override
    public Response proceed(@NonNull Request request) throws IOException {
        mCapturedRequest = request;
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_0)
                .code(mResponseCode)
                .message("Test")
                .build();
    }

    @Nullable
    @Override
    public Connection connection() {
        return mock(Connection.class);
    }

    /**
     * Returns the unaltered Request that would have first been returned by {@link #request()}. It is
     * assumed that the user of this Chain, namely an {@link Interceptor} will use the {@link #request()}
     * method to get the Request to modify. This would return the Request prior to your modifications.
     */
    public Request getInitialRequest() {
        return mInitialRequest;
    }

    /**
     * Returns the Request that was most recently passed to {@link #proceed(Request)}.
     */
    public Request getCapturedRequest() {
        return mCapturedRequest;
    }

    /**
     * Sets the response code which will be set on {@link Response} objects returned by {@link #proceed(Request)}.
     */
    public void setResponseCode(int code) {
        mResponseCode = code;
    }
}
