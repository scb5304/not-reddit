package com.jollyremedy.notreddit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.jollyremedy.notreddit.Constants;
import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.auth.accounting.Accountant;
import com.jollyremedy.notreddit.ui.main.MainActivity;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (getIntent() != null && getIntent().getData() != null) {
            //This activity is being started due to a login callback. The user may have decided to log
            //in from inside the app, which would result in this activity starting to consume
            //the intent.
            Log.wtf(TAG, "onCreate with intent data!");
            this.onLoginCallback(getIntent());
        } else {
            //This activity is being started due to the user adding an account from the settings
            //app on the device. We immediately defer to the Accountant to start logging in.
            Log.wtf(TAG, "onCreate without intent data! Calling Accountant's login() method.");
            Accountant.getInstance().login();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.wtf(TAG, "NEW INTENT!");
        if (intent.getData() != null) {
            Log.wtf(TAG, "New intent has data, login callback!");
            this.onLoginCallback(intent);
        }
    }

    private void onLoginCallback(Intent intent) {
        String uriString = intent.getDataString();

        Log.wtf(TAG, "Invoking login callback method and finishing.");
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

        verify();
    }

    public void completeWithError() {
        openMainActivity();
        Toast.makeText(this, "LOGIN FAILURE!", Toast.LENGTH_SHORT).show();
    }

    private void verify() {
        String currentUser = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.SharedPreferenceKeys.CURRENT_USERNAME_LOGGED_IN, null);
        Log.wtf(TAG, "Current user: " + currentUser);
        Log.wtf(TAG, "Current access token: " + Accountant.getInstance().getCurrentAccessToken());
        Log.wtf(TAG, "Current refresh token: " + Accountant.getInstance().getCurrentRefreshToken());
    }
}
