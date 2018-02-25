package com.jollyremedy.notreddit.util;

import android.content.Context;

import com.jollyremedy.notreddit.R;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

public class TimeDiffUtils {

    public static String readableSince(Context context, LocalDateTime dateTime) {
        long since;
        if ((since = minutesSince(dateTime)) < 60) {
            return context.getString(R.string.item_comment_since_minutes, since);
        } else if ((since = hoursSince(dateTime)) < 24) {
            return context.getString(R.string.item_comment_since_hours, since);
        } else if ((since = weeksSince(dateTime)) < 5) {
            return context.getString(R.string.item_comment_since_weeks, since);
        } else if ((since = monthsSince(dateTime)) < 12) {
            return context.getString(R.string.item_comment_since_months, since);
        } else {
            return context.getString(R.string.item_comment_since_years, since);
        }
    }

    private static long monthsSince(LocalDateTime dateTime) {
        return ChronoUnit.MONTHS.between(dateTime == null ? LocalDateTime.now() : dateTime, LocalDateTime.now());
    }

    private static long weeksSince(LocalDateTime dateTime) {
        return ChronoUnit.WEEKS.between(dateTime == null ? LocalDateTime.now() : dateTime, LocalDateTime.now());
    }

    private static long hoursSince(LocalDateTime dateTime) {
        return ChronoUnit.HOURS.between(dateTime == null ? LocalDateTime.now() : dateTime, LocalDateTime.now());
    }

    private static long minutesSince(LocalDateTime dateTime) {
        return ChronoUnit.MINUTES.between(dateTime == null ? LocalDateTime.now() : dateTime, LocalDateTime.now());
    }
}
