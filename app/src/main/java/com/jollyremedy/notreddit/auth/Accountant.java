package com.jollyremedy.notreddit.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.util.Log;

import com.jollyremedy.notreddit.api.AuthConstants;

import javax.inject.Inject;

public class Accountant {

    private AccountManager mAccountManager;
    private static final String TAG = "Accountant";
    private static final String ACCOUNT_NAME = "NotReddit";

    @Inject
    public Accountant(AccountManager accountManager) {
        mAccountManager = accountManager;
    }

    public boolean addAccount(String username, String password) {
        Account[] accounts = mAccountManager.getAccountsByType(AuthConstants.AUTH_ACCOUNT_TYPE);
        if (accounts.length == 0) {
            try {
                boolean added = mAccountManager.addAccountExplicitly(new Account(ACCOUNT_NAME, AuthConstants.AUTH_ACCOUNT_TYPE), null, null);
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
}
