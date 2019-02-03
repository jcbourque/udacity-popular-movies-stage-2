package com.example.android.popularmoviesstage2.utils;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmoviesstage2.BuildConfig;

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
    private static final String YOU_TUBE_SITE = "www.youtube.com";
    private static final String[] MOVIE_PATHS = new String[] {"3", "movie"};
    private static final String REVIEWS_PATH = "reviews";
    private static final String VIDEO_PATH = "videos";
    private static final String YOU_TUBE_PATH = "watch";
    private static final String KEY_QUERY_PARAM = "api_key";
    private static final String PAGE_QUERY_PARAM = "page";
    private static final String VIDEO_QUERY_PARAM = "v";

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

        return toURL(uri, "getMovieURL");
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

        return toURL(uri, "getPostersURL");
    }

    public static URL getReviewsURL(int id) {
        Uri.Builder builder = new Uri.Builder();

        builder.scheme(SECURE_SCHEME)
                .authority(MOVIE_SITE);

        for (String path : MOVIE_PATHS) {
            builder.appendPath(path);
        }

        builder.appendPath(String.valueOf(id))
                .appendPath(REVIEWS_PATH)
                .appendQueryParameter(KEY_QUERY_PARAM, BuildConfig.MOVIE_DB_API_KEY);

        Uri uri = builder.build();

        return toURL(uri, "getReviewsURL");
    }

    public static URL getVideosURL(int id) {
        Uri.Builder builder = new Uri.Builder();

        builder.scheme(SECURE_SCHEME)
                .authority(MOVIE_SITE);

        for (String path : MOVIE_PATHS) {
            builder.appendPath(path);
        }

        builder.appendPath(String.valueOf(id))
                .appendPath(VIDEO_PATH)
                .appendQueryParameter(KEY_QUERY_PARAM, BuildConfig.MOVIE_DB_API_KEY);

        Uri uri = builder.build();

        return toURL(uri, "getVideosURL");
    }

    public static Uri getYouTubeUri(String videoKey) {
        Uri.Builder builder = new Uri.Builder();

        return builder.scheme(SECURE_SCHEME)
                .authority(YOU_TUBE_SITE)
                .appendPath(YOU_TUBE_PATH)
                .appendQueryParameter(VIDEO_QUERY_PARAM, videoKey).build();
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
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

    public static void getResponseFromHttpUrl(final URL url, final NetCallback callback) {
        if (callback != null) {
            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection urlConnection = null;

                    try {
                        urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream in = urlConnection.getInputStream();

                        Scanner scanner = new Scanner(in);
                        scanner.useDelimiter("\\A");

                        callback.done(scanner.hasNext() ? scanner.next() : null);
                    } catch (IOException e) {
                        callback.error(e);
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                }
            });
        }
    }

    private static URL toURL(Uri uri, String method) {
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, method + ": Unable to build URL", e);
        }

        return url;
    }

    private NetUtils() {}
}
