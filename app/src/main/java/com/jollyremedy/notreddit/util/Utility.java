package com.jollyremedy.notreddit.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jollyremedy.notreddit.R;
import com.jollyremedy.notreddit.models.post.PostListingSort;

import java.io.InputStream;
import java.util.Scanner;

@SuppressWarnings("unused")
public class Utility {

    @SuppressWarnings("unused")
    public static String readStringFromJson(Context context, String fileName) {
        int jsonResId = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());
        InputStream ins = context.getResources().openRawResource(jsonResId);
        Scanner s = new Scanner(ins).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static boolean isEven (int integer) {
        return integer % 2 == 0;
    }

    /**
     * https://stackoverflow.com/a/10187511/4672234
     */
    public static CharSequence trimTrailingWhitespace(CharSequence source) {
        if (source == null) {
            return "";
        }

        int i = source.length();

        //noinspection StatementWithEmptyBody
        while (--i >= 0 && Character.isWhitespace(source.charAt(i))) {
            // loop back to the first non-whitespace character
        }

        return source.subSequence(0, i + 1);
    }

    @PostListingSort
    @Nullable
    public static String getPostListingSortFromDisplayedString(@NonNull Context context, @NonNull String displayedString) {
        if (context.getString(R.string.post_listing_sort_hot).equals(displayedString)) {
            return PostListingSort.HOT;
        } else if (context.getString(R.string.post_listing_sort_new).equals(displayedString)) {
            return PostListingSort.NEW;
        } else if (context.getString(R.string.post_listing_sort_best).equals(displayedString)) {
            return PostListingSort.BEST;
        } else if (context.getString(R.string.post_listing_sort_rising).equals(displayedString)) {
            return PostListingSort.RISING;
        } else if (context.getString(R.string.post_listing_sort_top).equals(displayedString)) {
            return PostListingSort.TOP;
        }  else if (context.getString(R.string.post_listing_sort_controversial).equals(displayedString)) {
            return PostListingSort.CONTROVERSIAL;
        }
        return null;
    }
}
