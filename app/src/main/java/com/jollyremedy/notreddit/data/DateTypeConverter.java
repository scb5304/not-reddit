package com.jollyremedy.notreddit.data;

import android.arch.persistence.room.TypeConverter;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;

public class DateTypeConverter {

    @TypeConverter
    public static LocalDateTime fromMillis(Long millis) {
        if (millis != null) {
            return LocalDateTime.from(Instant.ofEpochSecond(millis));
        } else {
            return null;
        }
    }

    @TypeConverter
    public static Long toMillis(LocalDateTime dateTime) {
        return dateTime == null ? null : Instant.from(dateTime).getEpochSecond();
    }
}