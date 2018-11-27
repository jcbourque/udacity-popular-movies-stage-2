package com.example.android.popularmoviesstage1.utils;

import android.net.Uri;

import com.example.android.popularmoviesstage1.BuildConfig;

public class NetUtils {
    public static void getMovies(MovieSort sort) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("sort_by", sort.getParameterValue())
                .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
                .build();
    }

    public static void getPoster(String image) {
        Uri uri = new Uri.Builder()
                .scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath(image)
                .build();
    }

    private NetUtils() {}
}
