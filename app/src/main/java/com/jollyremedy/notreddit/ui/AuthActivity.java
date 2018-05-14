package com.jollyremedy.notreddit.ui;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.auth.Accountant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthActivity extends AccountAuthenticatorActivity {

    @BindView(R.id.auth_login_username)
    TextView mUsernameTextView;

    @BindView(R.id.auth_login_password)
    TextView mPasswordTextView;

    private static final String TAG = "AuthActivity";
    private Accountant mAccountant;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        mAccountant = new Accountant(AccountManager.get(getApplicationContext()));
    }

    @OnClick(R.id.auth_login_button)
    void onLoginClicked() {
        String username = mUsernameTextView.getText().toString();
        String password = mPasswordTextView.getText().toString();

        boolean valid = true;

        if (Strings.isNullOrEmpty(username)) {
            mUsernameTextView.setError("Required field.");
            valid = false;
        }

        if (Strings.isNullOrEmpty(password)) {
            mPasswordTextView.setError("Required field.");
            valid = false;
        }

        if (valid) {
            Log.wtf(TAG, "Create account: TODO...");
            mAccountant.addAccount(username, password);
        } else {
            Log.w(TAG, "Not creating account.");
        }
    }
}
