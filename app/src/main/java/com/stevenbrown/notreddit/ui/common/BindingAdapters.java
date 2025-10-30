package com.stevenbrown.notreddit.ui.common;

import android.util.Patterns;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.google.common.base.Strings;
import com.stevenbrown.notreddit.di.GlideApp;
import com.stevenbrown.notreddit.util.NotRedditViewUtils;

import org.sufficientlysecure.htmltextview.HtmlResImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;
import org.sufficientlysecure.htmltextview.OnClickATagListener;

public class BindingAdapters {
    /**
     * This method will be used by data binding when we use app:html in XML.
     */
    @BindingAdapter(value = {"html", "onLinkClick"}, requireAll = false)
    public static void displayHtml(HtmlTextView view, @Nullable String html, @Nullable OnClickATagListener listener) {
        view.setHtml(Strings.nullToEmpty(html), new HtmlResImageGetter(view.getContext()));

        //These lines allow the parent layout to consume the click. Doesn't seem to work unless
        //programmatically set.
        view.setClickable(false);
        view.setLongClickable(false);
        if (listener != null) {
            view.setOnClickATagListener(listener);
        } else {
            view.setOnClickATagListener((widget, spannedText, href) -> false);
        }
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
        if (!Strings.isNullOrEmpty(url) && Patterns.WEB_URL.matcher(url).matches()) {
            GlideApp.with(view).load(url).into(view);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = (int) NotRedditViewUtils.convertDpToPixel(65);
            view.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = 0;
            view.setLayoutParams(layoutParams);
        }
    }
}
