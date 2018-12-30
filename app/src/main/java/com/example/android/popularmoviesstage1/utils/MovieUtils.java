package com.example.android.popularmoviesstage1.utils;

import android.util.Log;

import com.example.android.popularmoviesstage1.data.Movie;
import com.example.android.popularmoviesstage1.data.Poster;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieUtils {
    private static final String TAG = "MovieUtils";

    public static Movie getMovie(int id) {
        Movie movie = null;

        URL movieURL = NetUtils.getMovieURL(id);

        try {
            movie = JsonUtils.parseMovie(NetUtils.getResponseFromHttpUrl(movieURL));
        } catch (Exception e) {
            Log.e(TAG, "getMovie: Problem retrieving movie", e);
        }

        return movie;
    }

    public static List<Poster> getPosters(MovieSort sort, int page) {
        List<Poster> posters = new ArrayList<>();

        URL postersURL = NetUtils.getPostersURL(sort, page);

        try {
            posters.addAll(JsonUtils.parsePosters(NetUtils.getResponseFromHttpUrl(postersURL)));
        } catch (Exception e) {
            Log.e(TAG, "getPosters: Problem retrieving posters", e);
        }

        return posters;
    }

    private MovieUtils() {}
}
