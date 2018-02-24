package com.jollyremedy.notreddit.ui.api;

import android.support.annotation.Nullable;

import com.jollyremedy.notreddit.BaseUnitTest;
import com.jollyremedy.notreddit.StubbedOkHttpChain;
import com.jollyremedy.notreddit.Constants.SharedPreferenceKeys;
import com.jollyremedy.notreddit.api.OAuthTokenInterceptor;
import com.jollyremedy.notreddit.models.auth.Token;
import com.jollyremedy.notreddit.repository.TokenRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OAuthTokenInterceptorUnitTest extends BaseUnitTest {

    @Mock
    private TokenRepository mTokenRepository;

    private static final String TEST_TOKEN = "TOKEN1234-";
    private static final String TEST_AUTH_HEADER = "Bearer " + TEST_TOKEN;
    private static final String TEST_DEVICE_ID = "DEVICEID1234";

    private StubbedOkHttpChain mStubbedChain;
    private OAuthTokenInterceptor mOAuthTokenInterceptor;

    @Before
    public void setup() {
        //By default, there will be a valid token and valid device ID in shared preferences.
        when(mSharedPreferences.getString(eq(SharedPreferenceKeys.TOKEN), nullable(String.class))).thenReturn(TEST_TOKEN);
        when(mSharedPreferences.getString(eq(SharedPreferenceKeys.DEVICE_ID), nullable(String.class))).thenReturn(TEST_DEVICE_ID);
        mStubbedChain = new StubbedOkHttpChain("https://oauth.reddit.com");
        mOAuthTokenInterceptor = new OAuthTokenInterceptor(mSharedPreferences, mTokenRepository);
    }

    private void mockRepositoryToReturnToken(@Nullable String accessToken) {
        Token token = mock(Token.class);
        when(token.getAccessToken()).thenReturn(accessToken);
        when(mTokenRepository.getTokenSync(TEST_DEVICE_ID)).thenReturn(token);
    }

    @Test
    public void haveToken_makesRequestWithToken() throws Exception {
        mOAuthTokenInterceptor.intercept(mStubbedChain);
        Request capturedRequest = mStubbedChain.getCapturedRequest();
        assertEquals(capturedRequest.header("Authorization"), TEST_AUTH_HEADER);
    }

    @Test
    public void haveToken_tokenExpired_makesRequestWithNewToken() throws Exception {
        mStubbedChain.setResponseCode(HttpsURLConnection.HTTP_UNAUTHORIZED);
        mockRepositoryToReturnToken(TEST_TOKEN);

        mOAuthTokenInterceptor.intercept(mStubbedChain);
        verify(mTokenRepository).getTokenSync(TEST_DEVICE_ID);
        verify(mSharedPreferencesEditor).putString(SharedPreferenceKeys.TOKEN, TEST_TOKEN);

        Request capturedRequest = mStubbedChain.getCapturedRequest();
        assertEquals(capturedRequest.header("Authorization"), TEST_AUTH_HEADER);
    }

    @Test
    public void doNotHaveToken_makesRequestToGetToken_addsToRequest() throws Exception {
        when(mSharedPreferences.getString(eq(SharedPreferenceKeys.TOKEN), nullable(String.class))).thenReturn(null);
        mockRepositoryToReturnToken(TEST_TOKEN);

        mOAuthTokenInterceptor.intercept(mStubbedChain);
        verify(mTokenRepository).getTokenSync(TEST_DEVICE_ID);
        verify(mSharedPreferencesEditor).putString(SharedPreferenceKeys.TOKEN, TEST_TOKEN);

        Request capturedRequest = mStubbedChain.getCapturedRequest();
        assertEquals(capturedRequest.header("Authorization"), TEST_AUTH_HEADER);
    }

    @Test
    public void doNotHaveToken_proceedsWhenFailedToGetToken() throws Exception {
        when(mSharedPreferences.getString(eq(SharedPreferenceKeys.TOKEN), nullable(String.class))).thenReturn(null);
        mockRepositoryToReturnToken(null);

        mOAuthTokenInterceptor.intercept(mStubbedChain);
        verify(mTokenRepository).getTokenSync(TEST_DEVICE_ID);
        verify(mSharedPreferencesEditor).putString(SharedPreferenceKeys.TOKEN, null);
        assertEquals(mStubbedChain.getCapturedRequest(), mStubbedChain.getInitialRequest());
    }
}