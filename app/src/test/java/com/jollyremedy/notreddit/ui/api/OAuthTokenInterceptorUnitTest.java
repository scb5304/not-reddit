package com.jollyremedy.notreddit.ui.api;

import android.content.SharedPreferences;

import com.jollyremedy.notreddit.BaseUnitTest;
import com.jollyremedy.notreddit.CapturesRequestChain;
import com.jollyremedy.notreddit.Constants.SharedPreferenceKeys;
import com.jollyremedy.notreddit.api.OAuthTokenInterceptor;
import com.jollyremedy.notreddit.models.auth.Token;
import com.jollyremedy.notreddit.repository.TokenRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import okhttp3.Request;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OAuthTokenInterceptorUnitTest extends BaseUnitTest {

    @Mock
    private TokenRepository mTokenRepository;

    private static final String TEST_TOKEN = "TOKEN1234-";
    private static final String TEST_AUTH_HEADER = "Bearer " + TEST_TOKEN;
    private static final String TEST_DEVICE_ID = "DEVICEID1234";

    private CapturesRequestChain mMockChain;
    private OAuthTokenInterceptor mOAuthTokenInterceptor;

    @Before
    public void setup() {
        mMockChain = new CapturesRequestChain("https://oauth.reddit.com");
        mOAuthTokenInterceptor = new OAuthTokenInterceptor(mSharedPreferences, mTokenRepository);
    }

    @Test
    public void haveToken_makesRequestWithToken() throws Exception {
        when(mSharedPreferences.getString(eq(SharedPreferenceKeys.TOKEN), nullable(String.class))).thenReturn(TEST_TOKEN);
        mOAuthTokenInterceptor.intercept(mMockChain);
        Request capturedRequest = mMockChain.getCapturedRequest();
        assertEquals(capturedRequest.header("Authorization"), TEST_AUTH_HEADER);
    }

    @Test
    public void doNotHaveToken_makesRequestToGetToken_addsToRequest() throws Exception {
        when(mSharedPreferences.getString(eq(SharedPreferenceKeys.TOKEN), nullable(String.class))).thenReturn(null);
        when(mSharedPreferences.getString(eq(SharedPreferenceKeys.DEVICE_ID), nullable(String.class))).thenReturn(TEST_DEVICE_ID);

        Token token = mock(Token.class);
        when(token.getAccessToken()).thenReturn(TEST_TOKEN);
        when(mTokenRepository.getTokenSync(TEST_DEVICE_ID)).thenReturn(token);

        mOAuthTokenInterceptor.intercept(mMockChain);
        Request capturedRequest = mMockChain.getCapturedRequest();
        verify(mTokenRepository).getTokenSync(TEST_DEVICE_ID);
        assertEquals(capturedRequest.header("Authorization"), TEST_AUTH_HEADER);
    }
}
