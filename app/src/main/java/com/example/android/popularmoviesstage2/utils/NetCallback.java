package com.example.android.popularmoviesstage2.utils;

public interface NetCallback {
    void done(String response);

    void error(Exception e);
}
