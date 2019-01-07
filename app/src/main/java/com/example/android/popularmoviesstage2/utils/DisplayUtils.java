package com.example.android.popularmoviesstage2.utils;

import android.content.Context;
import android.util.DisplayMetrics;


public class DisplayUtils {
    public static int calculateNoOfColumns(Context context, int width) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / width);
    }

    private DisplayUtils() {}
}
