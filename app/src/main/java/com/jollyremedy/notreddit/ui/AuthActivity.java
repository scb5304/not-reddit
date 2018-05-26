package com.jollyremedy.notreddit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.auth.accounting.Accountant;
import com.jollyremedy.notreddit.ui.main.MainActivity;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().getData() != null) {
            //We're receiving the result of Reddit authentication (redirect URI).
            setContentView(R.layout.activity_auth);
            this.onLoginCallback(getIntent());
        } else {
            //The AccountAuthenticator is calling this activity for authenticate the user (create a new account).
            //Could be the user selecting "Add account" from the selection dialog, or "Add account" from android Settings.
            Accountant.getInstance().launchRedditAuthentication();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getData() != null) {
            this.onLoginCallback(intent);
        }
    }

    private void onLoginCallback(Intent intent) {
        String uriString = intent.getDataString();
        Accountant.getInstance().onLoginCallback(uriString, this);
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void completeSuccessfully() {
        openMainActivity();
        Toast.makeText(this, "LOGIN SUCCESS!", Toast.LENGTH_SHORT).show();
    }

    public void completeWithError() {
        openMainActivity();
        Toast.makeText(this, "LOGIN FAILURE!", Toast.LENGTH_SHORT).show();
    }
}
