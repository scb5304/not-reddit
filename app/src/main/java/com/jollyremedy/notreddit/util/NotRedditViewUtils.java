package com.jollyremedy.notreddit.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import com.jollyremedy.notreddit.R;

public class NotRedditViewUtils {

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static CustomTabsIntent createBaseCustomTabsIntent(Context context) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(ContextCompat.getColor(context, R.color.primary))
                .setShowTitle(true)
                .build();
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return customTabsIntent;
    }
}
