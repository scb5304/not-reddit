package com.jollyremedy.notreddit.api;

import com.jollyremedy.notreddit.BaseUnitTest;
import com.jollyremedy.notreddit.repository.TokenRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

public class OAuthTokenInterceptorUnitTest extends BaseUnitTest {

    @Mock
    private TokenRepository mTokenRepository;
    private OAuthTokenInterceptor mOAuthTokenInterceptor;

    @Before
    public void setup() {
        mOAuthTokenInterceptor = new OAuthTokenInterceptor(mSharedPreferences, mTokenRepository);
    }

    @Test
    public void testControl() {
        assertEquals(1L, 1L);
    }
}