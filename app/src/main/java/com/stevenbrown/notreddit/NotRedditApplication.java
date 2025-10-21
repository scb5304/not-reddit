package com.stevenbrown.notreddit;

import android.app.Application;
import android.content.SharedPreferences;

import com.facebook.stetho.Stetho;
import com.google.common.base.Strings;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.stevenbrown.notreddit.Constants.SharedPreferenceKeys;

import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class NotRedditApplication extends Application {

    @Inject
    SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        //AppInjector.init(this); TODO hilt
        Stetho.initializeWithDefaults(this); //TODO flipper?
        AndroidThreeTen.init(this);

        ensureHaveDeviceId();
       // registerActivityLifecycleCallbacks(new CustomTabsActivityLifecycleCallbacks()); TODO androidx.browser
    }

    private void ensureHaveDeviceId() {
        String deviceId = mSharedPreferences.getString(SharedPreferenceKeys.DEVICE_ID, null);
        if (Strings.isNullOrEmpty(deviceId)) {
            mSharedPreferences.edit()
                    .putString(SharedPreferenceKeys.DEVICE_ID, UUID.randomUUID().toString())
                    .apply();
        }
    }
}
