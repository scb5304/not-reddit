package com.jollyremedy.notreddit.api.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.lang.reflect.Type;

/**
 * A Unix timestamp is a number of seconds since 01-01-1970 00:00:00 GMT. Java measures time in
 * milliseconds since 01-01-1970 00:00:00 GMT. You need to multiply the Unix timestamp by 1000:
 * <a href="https://stackoverflow.com/a/29206856/4672234">StackOverflow link</a>.
 */
public class LocalDateTimeAdapter implements JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(json.getAsLong() * 1000), ZoneId.systemDefault());
    }
}
