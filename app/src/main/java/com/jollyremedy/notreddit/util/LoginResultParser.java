package com.jollyremedy.notreddit.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.base.Strings;

import javax.inject.Inject;

public class LoginResultParser {

    private static final String QUERY_PARAM_ERROR = "error";
    private static final String QUERY_PARAM_CODE = "code";
    private static final String QUERY_PARAM_STATE = "state";
    private static final String ERROR_ACCESS_DENIED = "access_denied";

    public LoginResultParser() {

    }

    public String getCode(String uri) {
        try {
            return Uri.parse(uri).getQueryParameter(QUERY_PARAM_CODE);
        } catch (Exception e) {
            return null;
        }
    }

    public String getError(String uri) {
        try {
            return Uri.parse(uri).getQueryParameter(QUERY_PARAM_ERROR);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isAccessDenied(String uri) {
        String error = getError(uri);
        return !Strings.isNullOrEmpty(error) && ERROR_ACCESS_DENIED.equals(error);
    }

    public String getState(String uri) {
        try {
            return Uri.parse(uri).getQueryParameter(QUERY_PARAM_STATE);
        } catch (Exception e) {
            return null;
        }
    }
}
