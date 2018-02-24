package com.jollyremedy.notreddit;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BaseUnitTest {

    @Mock
    protected Context mContext;

    @Mock
    protected SharedPreferences mSharedPreferences;

    @Mock
    protected SharedPreferences.Editor mSharedPreferencesEditor;

    @Before
    public void baseSetup() {
        initMocks(this);
        mockSharedPreferences();
    }

    private void mockSharedPreferences() {
        mSharedPreferences = mock(SharedPreferences.class);
        mSharedPreferencesEditor = mock(SharedPreferences.Editor.class, invocation -> mSharedPreferencesEditor);
        doNothing().when(mSharedPreferencesEditor).apply();
        when(mSharedPreferences.edit()).thenReturn(mSharedPreferencesEditor);
    }
}