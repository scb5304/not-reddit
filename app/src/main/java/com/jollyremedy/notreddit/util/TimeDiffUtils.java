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
        } else if ((since = daysSince(dateTime)) < 7) {
            return context.getString(R.string.item_comment_since_days, since);
        } else if ((since = weeksSince(dateTime)) < 5) {
            return context.getString(R.string.item_comment_since_weeks, since);
        } else if ((since = monthsSince(dateTime)) < 12) {
            return context.getString(R.string.item_comment_since_months, since);
        } else {
            return context.getString(R.string.item_comment_since_years, since);
        }
    }

    private static long monthsSince(LocalDateTime dateTime) {
        return unitSince(ChronoUnit.MONTHS, dateTime);
    }

    private static long weeksSince(LocalDateTime dateTime) {
        return unitSince(ChronoUnit.WEEKS, dateTime);
    }

    private static long daysSince(LocalDateTime dateTime) {
        return unitSince(ChronoUnit.DAYS, dateTime);
    }

    private static long hoursSince(LocalDateTime dateTime) {
        return unitSince(ChronoUnit.HOURS, dateTime);
    }

    private static long minutesSince(LocalDateTime dateTime) {
        return unitSince(ChronoUnit.MINUTES, dateTime);
    }

    private static long unitSince(ChronoUnit chronoUnit, LocalDateTime dateTime) {
        return chronoUnit.between(dateTime == null ? LocalDateTime.now() : dateTime, LocalDateTime.now());
    }
}
