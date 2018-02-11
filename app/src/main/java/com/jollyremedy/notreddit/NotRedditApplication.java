package com.jollyremedy.notreddit;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import com.facebook.stetho.Stetho;
import com.google.common.base.Strings;
import com.jollyremedy.notreddit.data.NotRedditDatabase;
import com.jollyremedy.notreddit.data.PostRepository;
import com.jollyremedy.notreddit.di.AppComponent;
import com.jollyremedy.notreddit.di.AppInjector;
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

    private NotRedditExecutors mAppExecutors;
    private static AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        AppInjector.init(this);
        Stetho.initializeWithDefaults(this);

        mAppComponent = DaggerAppComponent.builder()
                .application(this)
                .build();
        mAppComponent.inject(this);

        ensureHaveDeviceId();
        mAppExecutors = new NotRedditExecutors();
    }

    private void ensureHaveDeviceId() {
        String deviceId = mSharedPreferences.getString("device_id", null);
        if (Strings.isNullOrEmpty(deviceId)) {
            mSharedPreferences.edit()
                    .putString("device_id", UUID.randomUUID().toString())
                    .apply();
        }
    }

    public NotRedditDatabase getDatabase() {
        return NotRedditDatabase.getInstance(this, mAppExecutors);
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
