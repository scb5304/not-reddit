package com.jollyremedy.notreddit.auth.accounting;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jollyremedy.notreddit.Constants;
import com.jollyremedy.notreddit.NotRedditApplication;
import com.jollyremedy.notreddit.api.AuthConstants;
import com.jollyremedy.notreddit.models.auth.Token;
import com.jollyremedy.notreddit.repository.FullTokenRepository;
import com.jollyremedy.notreddit.ui.AuthActivity;

import javax.inject.Inject;

public class Accountant {

    public static final int CHOOSE_ACCOUNT_REQUEST_CODE = 5;

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

    /**
     * Prompts the user to select the NotReddit account to log in to. If there are no accounts,
     * the user is immediately taken to the reddit authentication page to create a new account, which
     * is then logged in to.
     */
    public void login(Activity activity) {
        Account[] accounts = mAccountManager.getAccountsByType(AuthConstants.AUTH_ACCOUNT_TYPE);
        if (accounts.length == 0) {
            Log.wtf(TAG, "No accounts!");
            mContext.startActivity(new Intent(mContext, AuthActivity.class));
        } else {
            Intent chooseAccountIntent = AccountManager.newChooseAccountIntent(
                    getCurrentAccount(),
                    null,
                    new String[]{AuthConstants.AUTH_ACCOUNT_TYPE},
                    null,
                    AuthConstants.AUTH_GRANT_TYPE_CODE,
                    null,
                    null
            );
            activity.startActivityForResult(chooseAccountIntent, CHOOSE_ACCOUNT_REQUEST_CODE);
        }
    }

    public void logout() {
        mSharedPreferences.edit()
                .remove(Constants.SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN)
                .remove(Constants.SharedPreferenceKeys.TEMP_USER_TOKEN)
                .apply();
        Toast.makeText(mContext, "You have been logged out.", Toast.LENGTH_SHORT).show();
    }

    private void addAccessTokenToAccount(Account account, @NonNull String accessToken) {
        mAccountManager.setAuthToken(account, AuthConstants.AUTH_GRANT_TYPE_CODE, accessToken);
    }

    private void addRefreshTokenToAccount(Account account, @NonNull String refreshToken) {
        mAccountManager.setUserData(account, AuthConstants.USER_DATA_KEY_REFRESH_TOKEN, refreshToken);
    }

    private void addTokenToAccount(Account account, @NonNull Token token) {
        addAccessTokenToAccount(account, token.getAccessToken());
        addRefreshTokenToAccount(account, token.getRefreshToken());
    }

    public void updateCurrentAccessToken(String accessToken) {
        Account currentAccount = getCurrentAccount();
        if (currentAccount != null) {
            addAccessTokenToAccount(currentAccount, accessToken);
        }
    }

    private boolean addAccount(Token token) {
        String username = token.getAccount().getName();

        try {
            Account userAccount = new Account(username, AuthConstants.AUTH_ACCOUNT_TYPE);
            boolean added = mAccountManager.addAccountExplicitly(userAccount, null, null);
            if (added) {
                addTokenToAccount(userAccount, token);
                Log.d(TAG, "Added a NotReddit account for " + username);
                return true;
            } else {
                Log.e(TAG, "Unable to add a NotReddit account: already exists? Trying to remove.");
                boolean removed = mAccountManager.removeAccountExplicitly(userAccount);

                if (removed) {
                    Log.d(TAG, "Removed old NotReddit account for " + username);
                    boolean addedAttemptTwo = mAccountManager.addAccountExplicitly(userAccount, null, null);

                    if (addedAttemptTwo) {
                        addTokenToAccount(userAccount, token);
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
                            authUi.completeSuccessfully(token.getAccount().getName());
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
