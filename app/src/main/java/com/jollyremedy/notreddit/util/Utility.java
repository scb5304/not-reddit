package com.jollyremedy.notreddit.util;

import android.content.Context;

import java.io.InputStream;
import java.util.Scanner;

public class Utility {
    public static String readStringFromJson(Context context, String fileName) {
        int jsonResId = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());
        InputStream ins = context.getResources().openRawResource(jsonResId);
        Scanner s = new Scanner(ins).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
