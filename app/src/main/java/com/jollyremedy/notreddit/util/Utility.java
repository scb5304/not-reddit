package com.jollyremedy.notreddit.util;

import android.content.Context;

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
}
