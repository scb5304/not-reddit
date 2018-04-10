package com.jollyremedy.notreddit.ui.common;

import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.common.base.Strings;
import com.jollyremedy.notreddit.di.GlideApp;

import org.sufficientlysecure.htmltextview.HtmlResImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

public class BindingAdapters {
    /**
     * This method will be used by data binding when we use app:html in XML.
     */
    @BindingAdapter({"html"})
    public static void displayHtml(HtmlTextView view, @Nullable String html) {
        view.setHtml(Strings.nullToEmpty(html), new HtmlResImageGetter(view));

        //These lines allow the parent layout to consume the click. Doesn't seem to work unless
        //programmatically set.
        view.setClickable(false);
        view.setLongClickable(false);
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
        if (!Strings.isNullOrEmpty(url) && !url.equalsIgnoreCase("self")) {
            GlideApp.with(view).load(url).into(view);
        } else {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = 0;
            view.setLayoutParams(layoutParams);
        }
    }
}
