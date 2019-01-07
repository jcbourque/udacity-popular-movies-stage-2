package com.example.android.popularmoviesstage2.utils;

import android.util.Log;

import com.example.android.popularmoviesstage2.data.Movie;
import com.example.android.popularmoviesstage2.data.Poster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JsonUtils
{
    private static final String TAG = "JsonUtils";

    public static Movie parseMovie(String json) {
        Movie movie;

        try {
            JSONObject data = new JSONObject(json);

            movie = new Movie();

            movie.setVoteCount(data.optInt("vote_count"));
            movie.setId(data.optInt("id"));
            movie.setVideo(data.optBoolean("video"));
            movie.setVoteAverage(data.optDouble("vote_average"));
            movie.setTitle(data.optString("title"));
            movie.setPopularity(data.optDouble("popularity"));
            movie.setPosterPath(data.optString("poster_path"));
            movie.setOriginalLanguage(data.optString("original_language"));
            movie.setOriginalTitle(data.optString("original_title"));
            movie.setGenreIds(toIntList(data.optJSONArray("genre_ids")));
            movie.setBackdropPath(data.optString("backdrop_path"));
            movie.setAdult(data.optBoolean("adult"));
            movie.setOverview(data.optString("overview"));
            movie.setReleaseDate(toDate(data.optString("release_date"), "yyyy-MM-dd"));
            movie.setRunTime(data.optInt("runtime"));
        } catch (JSONException e) {
            Log.e(TAG, "parseMovie: Unable to parse movie", e);
            movie = null;
        }

        return movie;
    }

    public static Poster parsePoster(String json) {
        Poster poster;

        try {
            JSONObject data = new JSONObject(json);

            poster = new Poster();

            poster.setId(data.optInt("id"));
            poster.setPath(data.optString("poster_path"));
            poster.setTitle(data.optString("title"));
        } catch (JSONException e) {
            Log.e(TAG, "parsePoster: Unable to parse poster", e);
            poster = null;
        }

        return poster;
    }

    public static List<Poster> parsePosters(String json) {
        List<Poster> posters = new ArrayList<>();

        try {
            JSONObject data = new JSONObject(json);
            JSONArray results = data.optJSONArray("results");

            if (results != null) {
                for (int i = 0; i < results.length(); i++) {
                    posters.add(parsePoster(results.optString(i)));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "parsePosters: Unable to parse posters", e);
        }

        return posters;
    }

    public static Date toDate(String value, String pattern) {
        Date date = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
            date = sdf.parse(value);
        } catch (Exception e) {
            Log.e(TAG, "toDate: A parsing exception has occurred", e);
        }

        return date;
    }

    public static List<Integer> toIntList(JSONArray array) {
        List<Integer> list = new ArrayList<>();

        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                list.add(array.optInt(i));
            }
        }

        return list;
    }

    public static List<String> toStringList(JSONArray array) {
        List<String> list = new ArrayList<>();

        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                list.add(array.optString(i));
            }
        }

        return list;
    }

    private JsonUtils() {}
}
