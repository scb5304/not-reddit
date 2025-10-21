package com.stevenbrown.notreddit.ui;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.stevenbrown.notreddit.BuildConfig;
import com.stevenbrown.notreddit.Constants;
import com.stevenbrown.notreddit.R;
import com.stevenbrown.notreddit.auth.accounting.Accountant;
import com.stevenbrown.notreddit.databinding.ActivityAuthBinding;
import com.stevenbrown.notreddit.ui.main.MainActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends AppCompatActivity {

    @Inject
    Accountant mAccountant;

    private WebView mWebView;
    private SharedPreferences mSharedPreferences;

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            boolean notRedditScheme = request.getUrl().getScheme().equalsIgnoreCase(getString(R.string.oauth_scheme));
            if (notRedditScheme) {
                onLoginCallback(request.getUrl().toString());
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAuthBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_auth);
        mWebView = binding.authWebview;

        CookieManager.getInstance().removeAllCookies(value -> launchRedditAuthentication());
        mWebView.setWebViewClient(mWebViewClient);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void launchRedditAuthentication() {
        String url = buildRedditLoginUrl(mSharedPreferences.getString(Constants.SharedPreferenceKeys.DEVICE_ID, null));
        mWebView.loadUrl(url);
    }

    private String buildRedditLoginUrl(String state) {
        return "https://www.reddit.com/api/v1/authorize.compact" +
                "?client_id=" + BuildConfig.CLIENT_ID +
                "&response_type=" + "code" +
                "&state=" + state +
                "&duration=" + "permanent" +
                "&redirect_uri=" + BuildConfig.REDIRECT_URI +
                "&scope=" + "identity mysubreddits read";
    }

    private void onLoginCallback(String uriString) {
        mAccountant.onLoginCallback(uriString, this);
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void completeSuccessfully(String username) {
        openMainActivity();
        Toast.makeText(this, getString(R.string.login_success, username), Toast.LENGTH_SHORT).show();
    }

    public void completeWithError() {
        openMainActivity();
        Toast.makeText(this, getString(R.string.login_failure), Toast.LENGTH_LONG).show();
    }
}
