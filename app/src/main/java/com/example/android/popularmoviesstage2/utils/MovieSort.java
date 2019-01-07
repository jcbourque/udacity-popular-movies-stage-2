package com.example.android.popularmoviesstage2.utils;

public enum MovieSort {
    MOST_POPULAR("popular"),
    TOP_RATED("top_rated");

    private final String path;

    MovieSort(String value) {
        path = value;
    }

    public String getPath() {
        return path;
    }
}
