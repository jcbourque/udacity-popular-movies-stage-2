package com.example.android.popularmoviesstage1.utils;

public enum MovieSort {
    MOST_POPULAR("popularity.desc"),
    TOP_RATED("vote_average.desc");

    private final String parameterValue;

    MovieSort(String value) {
        parameterValue = value;
    }

    public String getParameterValue() {
        return  parameterValue;
    }
}
