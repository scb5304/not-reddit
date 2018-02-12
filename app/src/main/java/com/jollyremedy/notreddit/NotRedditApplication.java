package com.jollyremedy.notreddit;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import com.facebook.stetho.Stetho;
import com.google.common.base.Strings;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.jollyremedy.notreddit.Constants.SharedPreferenceKeys;
import com.jollyremedy.notreddit.db.NotRedditDatabase;
import com.jollyremedy.notreddit.di.AppComponent;
import com.jollyremedy.notreddit.di.auto.AppInjector;
import com.jollyremedy.notreddit.di.DaggerAppComponent;

import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class NotRedditApplication extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    SharedPreferences mSharedPreferences;

    private static AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        AppInjector.init(this);
        Stetho.initializeWithDefaults(this);
        AndroidThreeTen.init(this);

        mAppComponent = DaggerAppComponent.builder()
                .application(this)
                .build();
        mAppComponent.inject(this);

        ensureHaveDeviceId();
    }

    private void ensureHaveDeviceId() {
        String deviceId = mSharedPreferences.getString(SharedPreferenceKeys.DEVICE_ID, null);
        if (Strings.isNullOrEmpty(deviceId)) {
            mSharedPreferences.edit()
                    .putString(SharedPreferenceKeys.DEVICE_ID, UUID.randomUUID().toString())
                    .apply();
        }
    }

    public NotRedditDatabase getDatabase() {
        return NotRedditDatabase.getInstance(this);
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
