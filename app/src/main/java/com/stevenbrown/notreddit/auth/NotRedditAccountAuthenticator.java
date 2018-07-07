package com.stevenbrown.notreddit.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.common.base.Strings;
import com.stevenbrown.notreddit.api.AuthConstants;
import com.stevenbrown.notreddit.ui.AuthActivity;

public class NotRedditAccountAuthenticator extends AbstractAccountAuthenticator {

    private final AccountManager mAccountManager;
    private final Context mContext;

    NotRedditAccountAuthenticator(Context context) {
        super(context);
        mContext = context;
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) {
        final Intent intent = new Intent(mContext, AuthActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountManager.KEY_AUTH_TOKEN_LABEL, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) {
        String authToken = mAccountManager.peekAuthToken(account, authTokenType);
        String refreshToken = mAccountManager.getUserData(account, AuthConstants.USER_DATA_KEY_REFRESH_TOKEN);

        final Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_AUTH_TOKEN_LABEL, authTokenType);

        //return an auth token if it exists
        if (!Strings.isNullOrEmpty(authToken)) {
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            result.putString(AuthConstants.USER_DATA_KEY_REFRESH_TOKEN, refreshToken);
            return result;
        }

        //no token -- re-prompt for credentials
        final Intent intent = new Intent(mContext, AuthActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        result.putParcelable(AccountManager.KEY_INTENT, intent);
        return result;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return "Label: " + authTokenType;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) {
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }
}
