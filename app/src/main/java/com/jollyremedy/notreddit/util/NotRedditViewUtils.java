package com.jollyremedy.notreddit.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

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
}
