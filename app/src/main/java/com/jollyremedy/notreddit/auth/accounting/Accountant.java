package com.jollyremedy.notreddit.auth.accounting;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;

import com.jollyremedy.notreddit.BuildConfig;
import com.jollyremedy.notreddit.Constants;
import com.jollyremedy.notreddit.api.AuthConstants;
import com.jollyremedy.notreddit.ui.main.MainActivity;
import com.jollyremedy.notreddit.util.LoginResultParser;
import com.jollyremedy.notreddit.util.NotRedditViewUtils;

import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

public class Accountant {

    //Using application context.
    @SuppressLint("StaticFieldLeak")
    private static Accountant sInstance;

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private AccountManager mAccountManager;
    private static final String TAG = "Accountant";

    //TODO: Use this constructor for unit tests?
    private Accountant(Context context, SharedPreferences sharedPreferences, AccountManager accountManager) {
        mContext = context;
        mSharedPreferences = sharedPreferences;
        mAccountManager = accountManager;
    }

    public static Accountant getInstance(@NonNull Context context) {
        if (sInstance == null) {
            context = context.getApplicationContext();
            sInstance = new Accountant(context, PreferenceManager.getDefaultSharedPreferences(context), AccountManager.get(context));
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
                "&scope=" + "vote mysubreddits";
    }

    private boolean addAccount(String username, String password) {
        Account[] accounts = mAccountManager.getAccountsByType(AuthConstants.AUTH_ACCOUNT_TYPE);
        if (accounts.length == 0) {
            try {
                boolean added = mAccountManager.addAccountExplicitly(new Account(username, AuthConstants.AUTH_ACCOUNT_TYPE), null, null);
                if (added) {
                    Log.d(TAG, "Added a NotReddit account.");
                } else {
                    Log.e(TAG, "Unable to add a NotReddit account.");
                }
                return added;
            } catch (Exception e) {
                Log.e(TAG, "Unable to add a NotReddit account.", e);
            }
            return false;
        } else {
            Log.w(TAG, "Account already exists.");
        }
        return false;
    }

    public void onLoginCallback(String uriString) {
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
            //todo: error message?
        } else {
            Log.e(TAG, "Okay, everything seems fine: " + uriString);
            Log.wtf(TAG, "Get token from auth code.");
        }

        openMainActivity();
    }

    private void openMainActivity() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
    }

    /*
     mTokenRepository.getToken(loginResultParser.getCode(uriString))
     .subscribe(token -> {
     Log.wtf(TAG, "Got it! " + new Gson().toJson(token));
     mSharedPreferences.edit().putString(Constants.SharedPreferenceKeys.TOKEN, token.getAccessToken()).apply();
     tryIt();
     }, throwable -> {
     Log.wtf(TAG, "Uh oh!!!", throwable);
     });


     private void tryIt() {
     mSubredditRepository.getSubredditsForUserWhere(SubredditForUserWhere.SUBSCRIBER, new SubredditListingObserver());
     }

     */
}
