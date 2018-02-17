package com.jollyremedy.notreddit.ui.common;

import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

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
}
