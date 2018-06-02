package com.jollyremedy.notreddit;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.google.common.base.Strings;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.jollyremedy.notreddit.Constants.SharedPreferenceKeys;
import com.jollyremedy.notreddit.di.AppComponent;
import com.jollyremedy.notreddit.di.DaggerAppComponent;
import com.jollyremedy.notreddit.di.auto.AppInjector;

import java.net.UnknownHostException;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import saschpe.android.customtabs.CustomTabsActivityLifecycleCallbacks;
import timber.log.Timber;

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

        if (BuildConfig.DEBUG) {
            Timber.plant(new NotRedditDebugTree());
        }

        ensureHaveDeviceId();
        registerActivityLifecycleCallbacks(new CustomTabsActivityLifecycleCallbacks());
    }

    private void ensureHaveDeviceId() {
        String deviceId = mSharedPreferences.getString(SharedPreferenceKeys.DEVICE_ID, null);
        if (Strings.isNullOrEmpty(deviceId)) {
            mSharedPreferences.edit()
                    .putString(SharedPreferenceKeys.DEVICE_ID, UUID.randomUUID().toString())
                    .apply();
        }
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    private class NotRedditDebugTree extends Timber.DebugTree {
        /**
         * I want to see UnknownHostExceptions logged and Android does not.
         * @see <a href="https://github.com/aosp-mirror/platform_frameworks_base/commit/dba50c7ed24e05ff349a94b8c4a6d9bb9050973b" >GitHub commit</a>.
         */
        private boolean logHostExceptions(Throwable t) {
            if (t instanceof UnknownHostException) {
                Timber.e(Log.getStackTraceString(t));
                return true;
            }
            return false;
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            if (logHostExceptions(t)) {
                return;
            }
            super.e(t, message, args);
        }

        @Override
        public void e(Throwable t) {
            if (logHostExceptions(t)) {
                return;
            }
            super.e(t);
        }
    }
}
