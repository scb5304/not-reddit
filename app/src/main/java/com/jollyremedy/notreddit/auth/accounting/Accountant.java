package com.jollyremedy.notreddit.auth.accounting;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;

import com.google.gson.Gson;
import com.jollyremedy.notreddit.BuildConfig;
import com.jollyremedy.notreddit.Constants;
import com.jollyremedy.notreddit.NotRedditApplication;
import com.jollyremedy.notreddit.api.AuthConstants;
import com.jollyremedy.notreddit.models.auth.Token;
import com.jollyremedy.notreddit.repository.FullTokenRepository;
import com.jollyremedy.notreddit.ui.AuthActivity;
import com.jollyremedy.notreddit.ui.main.MainActivity;
import com.jollyremedy.notreddit.util.LoginResultParser;
import com.jollyremedy.notreddit.util.NotRedditViewUtils;

import java.net.BindException;

import javax.inject.Inject;

import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

public class Accountant {

    //Using application context.
    @SuppressLint("StaticFieldLeak")
    private static Accountant sInstance;

    @Inject
    Context mContext;

    @Inject
    SharedPreferences mSharedPreferences;

    @Inject
    AccountManager mAccountManager;

    @Inject
    FullTokenRepository mFullTokenRepository;

    private static final String TAG = "Accountant";

    private Accountant() {
        NotRedditApplication.getAppComponent().inject(this);
    }

    public static Accountant getInstance() {
        if (sInstance == null) {
            sInstance = new Accountant();
        }
        return sInstance;
    }

    public void login() {
        CustomTabsIntent customTabsIntent = NotRedditViewUtils.createBaseCustomTabsIntent(mContext);
        CustomTabsHelper.addKeepAliveExtra(mContext, customTabsIntent.intent);

        String deviceId = mSharedPreferences.getString(Constants.SharedPreferenceKeys.DEVICE_ID, "");
        String url = buildRedditLoginUrl(deviceId);

        CustomTabsHelper.openCustomTab(mContext, customTabsIntent,
                Uri.parse(url),
                new WebViewFallback());
    }

    private String buildRedditLoginUrl(String state) {
        return "https://www.reddit.com/api/v1/authorize.compact" +
                "?client_id=" + BuildConfig.CLIENT_ID +
                "&response_type=" + "code" +
                "&state=" + state +
                "&duration=" + "permanent" +
                "&redirect_uri=" + BuildConfig.REDIRECT_URI +
                "&scope=" + "vote mysubreddits identity";
    }

    private boolean addAccount(Token token) {
        String username = token.getAccount().getName();
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();

        try {
            Account userAccount = new Account(username, AuthConstants.AUTH_ACCOUNT_TYPE);
            boolean added = mAccountManager.addAccountExplicitly(userAccount, null, null);
            if (added) {
                Log.d(TAG, "Added a NotReddit account for " + username);
                return true;
            } else {
                Log.e(TAG, "Unable to add a NotReddit account: already exists? Trying to remove.");
                boolean removed = mAccountManager.removeAccountExplicitly(userAccount);

                if (removed) {
                    Log.d(TAG, "Removed old NotReddit account for " + username);
                    Bundle userData = new Bundle();
                    userData.putString(AuthConstants.USER_DATA_KEY_REFRESH_TOKEN, refreshToken);

                    boolean addedAttemptTwo = mAccountManager.addAccountExplicitly(userAccount, null, userData);
                    mAccountManager.setAuthToken(userAccount, AuthConstants.AUTH_GRANT_TYPE_CODE, accessToken);

                    if (addedAttemptTwo) {
                        Log.d(TAG, "Added the account after removing the original.");
                        return true;
                    }
                } else {
                    Log.e(TAG, "Failed to remove old NotReddit account for " + username);
                }
            }
            Log.e(TAG, "Unable to create account.");
        } catch (Exception e) {
            Log.e(TAG, "Fatal internal error while creating account.", e);
        }
        return false;
    }

    public void onLoginCallback(String uriString, AuthActivity authUi) {
        String deviceId = mSharedPreferences.getString(Constants.SharedPreferenceKeys.DEVICE_ID, "");
        LoginResultParser loginResultParser = new LoginResultParser();

        if (uriString == null) {
            Log.e(TAG, "Uh oh!! Redirect URI string is null.");
            return;
        }

        if (!deviceId.equals(loginResultParser.getState(uriString))) {
            Log.e(TAG, "Uh oh! They didn't pass back the device ID we sent up as 'state'.");
            return;
        }

        if (loginResultParser.isAccessDenied(uriString)) {
            Log.e(TAG, "They turned us down!");
        } else {
            mFullTokenRepository.getFullToken(loginResultParser.getCode(uriString))
                    .doFinally(() -> mSharedPreferences.edit()
                            .remove(Constants.SharedPreferenceKeys.TEMP_USER_TOKEN)
                            .apply())
                    .subscribe(token -> {

                        Log.wtf(TAG, "Got it!! " + new Gson().toJson(token));

                        boolean addSuccess = addAccount(token);
                        if (addSuccess) {
                            mSharedPreferences.edit()
                                    .putString(Constants.SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN, token.getAccount().getName())
                                    .apply();
                            authUi.completeSuccessfully();
                        } else {
                            authUi.completeWithError();
                            mSharedPreferences.edit()
                                    .remove(Constants.SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN)
                                    .apply();
                        }
                    }, throwable -> {
                        Log.wtf(TAG, "Error during token orchestration.", throwable);
                        authUi.completeWithError();
                    });
        }
    }

    @Nullable
    private Account getCurrentAccount() {
        String currentUsername = mSharedPreferences.getString(Constants.SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN, null);
        Account[] accounts = mAccountManager.getAccountsByType(AuthConstants.AUTH_ACCOUNT_TYPE);
        if (accounts.length == 0) {
            Log.wtf(TAG, "No accounts!");
            return null;
        }

        if (currentUsername == null) {
            Log.wtf(TAG, "No current user!");
            return null;
        }

        for (Account account : accounts) {
            Log.d(TAG, "Looking at account: " + account.name);
            if (account.name.equalsIgnoreCase(currentUsername)) {
                return account;
            }
        }

        return null;
    }

    @Nullable
    public String getCurrentAccessToken() {
        Account currentAccount = getCurrentAccount();
        if (currentAccount == null) {
            Log.wtf(TAG, "No current account to get access token with!");
            return null;
        }

        return mAccountManager.peekAuthToken(currentAccount, AuthConstants.AUTH_GRANT_TYPE_CODE);
    }

    @Nullable
    public String getCurrentRefreshToken() {
        Account currentAccount = getCurrentAccount();
        if (currentAccount == null) {
            Log.wtf(TAG, "No current account to get refresh token with!");
            return null;
        }

        return mAccountManager.getUserData(currentAccount, AuthConstants.USER_DATA_KEY_REFRESH_TOKEN);
    }
}
