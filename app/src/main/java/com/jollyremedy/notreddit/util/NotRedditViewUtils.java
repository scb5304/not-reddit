package com.jollyremedy.notreddit.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;

import com.google.common.base.Strings;
import com.jollyremedy.notreddit.R;

public class NotRedditViewUtils {

    /**
     * {@see https://stackoverflow.com/a/41201865}
     */
    public static void applyHorizontalItemDecorationToRecyclerView(Context context, RecyclerView recyclerView) {
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(context, R.drawable.horizontal_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static Spanned fromHtml(String html){
        Spanned result;
        html = Strings.nullToEmpty(html);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
