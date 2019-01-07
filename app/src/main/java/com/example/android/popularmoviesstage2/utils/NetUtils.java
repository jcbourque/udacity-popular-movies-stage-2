package com.example.android.popularmoviesstage2.utils;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmoviesstage1.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetUtils {
    private static final String TAG = "NetUtils";
    private static final String SECURE_SCHEME = "https";
    private static final String MOVIE_SITE = "api.themoviedb.org";
    private static final String[] MOVIE_PATHS = new String[] {"3", "movie"};
    private static final String KEY_QUERY_PARAM = "api_key";
    private static final String PAGE_QUERY_PARAM = "page";

    // TODO May need to do away with MovieSort and create 2 methods

    public static URL getMovieURL(int id) {
        Uri.Builder builder = new Uri.Builder();

        builder.scheme(SECURE_SCHEME)
                .authority(MOVIE_SITE);

        for (String path : MOVIE_PATHS) {
            builder.appendPath(path);
        }

        builder.appendPath(String.valueOf(id))
                .appendQueryParameter(KEY_QUERY_PARAM, BuildConfig.MOVIE_DB_API_KEY);

        Uri uri = builder.build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "getMovieURL: Unable to build URL", e);
        }

        return url;
    }

    public static URL getPostersURL(MovieSort sort, int page) {
        Uri.Builder builder =  new Uri.Builder();

        builder.scheme(SECURE_SCHEME)
                .authority(MOVIE_SITE);

        for (String path : MOVIE_PATHS) {
            builder.appendPath(path);
        }

        builder.appendPath(sort.getPath())
                .appendQueryParameter(PAGE_QUERY_PARAM, String.valueOf(page))
                .appendQueryParameter(KEY_QUERY_PARAM, BuildConfig.MOVIE_DB_API_KEY);

        Uri uri = builder.build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "getMovieURL: Unable to build URL", e);
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Log.i(TAG, "getResponseFromHttpUrl: URL " + url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            return scanner.hasNext() ? scanner.next() : null;
        } finally {
            urlConnection.disconnect();
        }
    }

    private NetUtils() {}
}
