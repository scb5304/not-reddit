package com.stevenbrown.notreddit.api.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.stevenbrown.notreddit.models.comment.CommentListing;
import com.stevenbrown.notreddit.models.comment.PostWithCommentListing;
import com.stevenbrown.notreddit.models.post.PostListing;

import java.lang.reflect.Type;


/**
 * Reddit's comments endpoint returns an array of length 2: the first element is a PostListing,
 * and the second one is a CommentListing.
 */
public class SensicalCommentsAdapter implements JsonDeserializer<PostWithCommentListing> {
    @Override
    public PostWithCommentListing deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray uglyArray = json.getAsJsonArray();
        if (uglyArray.size() < 2) {
            throw new JsonParseException("Expected a PostWithCommentListing to contain at least 2 elements.");
        }

        PostListing postListing = context.deserialize(uglyArray.get(0), PostListing.class);
        CommentListing commentListing = context.deserialize(uglyArray.get(1), CommentListing.class);

        return new PostWithCommentListing(postListing, commentListing);
    }
}
