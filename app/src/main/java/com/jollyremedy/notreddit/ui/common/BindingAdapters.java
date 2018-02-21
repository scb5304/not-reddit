package com.jollyremedy.notreddit.ui.common;

import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.google.common.base.Strings;
import com.squareup.picasso.Picasso;

import org.sufficientlysecure.htmltextview.HtmlResImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

public class BindingAdapters {
    /**
     * This method will be used by data binding when we use app:html in XML.
     */
    @BindingAdapter({"html"})
    public static void displayHtml(HtmlTextView view, @Nullable String html) {
        view.setHtml(Strings.nullToEmpty(html), new HtmlResImageGetter(view));
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
        if (!Strings.isNullOrEmpty(url) && !url.equalsIgnoreCase("self")) {
            view.setTag(view);
            Picasso.with(view.getContext()).load(url).into(view);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
