package com.example.android.popularmoviesstage2.utils;

import android.util.Log;

import com.example.android.popularmoviesstage2.data.Movie;
import com.example.android.popularmoviesstage2.data.Poster;
import com.example.android.popularmoviesstage2.data.Review;
import com.example.android.popularmoviesstage2.data.Video;
import com.example.android.popularmoviesstage2.data.VideoType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class JsonUtils
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
            movie.setReleaseDate(toDate(data.optString("release_date")));
            movie.setRunTime(data.optInt("runtime"));
        } catch (JSONException e) {
            Log.e(TAG, "parseMovie: Unable to parse movie", e);
            movie = null;
        }

        return movie;
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

    public static List<Review> parseReviews(String json) {
        List<Review> reviews = new ArrayList<>();

        try {
            JSONObject data = new JSONObject(json);
            JSONArray results = data.optJSONArray("results");

            if (results != null) {
                for (int i = 0; i < results.length(); i++) {
                    reviews.add(parseReview(results.optString(i)));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseReviews: Unable to parse reviews", e);
        }

        return reviews;
    }

    public static List<Video> parseVideos(String json) {
        List<Video> videos = new ArrayList<>();

        try {
            JSONObject data = new JSONObject(json);
            JSONArray results = data.optJSONArray("results");

            if (results != null) {
                for (int i = 0; i < results.length(); i++) {
                    videos.add(parseVideo(results.optString(i)));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseVideos: Unable to parse videos", e);
        }

        return videos;
    }

    private JsonUtils() {}

    private static Poster parsePoster(String json) {
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

    private static Review parseReview(String json) {
        Review review;

        try {
            JSONObject data = new JSONObject(json);

            review = new Review();

            review.setId(data.optString("id"));
            review.setAuthor(data.optString("author"));
            review.setContent(data.optString("content"));
            review.setUrl(data.optString("url"));
        } catch (JSONException e) {
            Log.e(TAG, "parseVideo: Unable to parse video", e);
            review = null;
        }

        return review;
    }

    private static Video parseVideo(String json) {
        Video video;

        try {
            JSONObject data = new JSONObject(json);

            video = new Video();

            video.setId(data.optString("id"));
            video.setIsoLanguage(data.optString("iso_639_1"));
            video.setIsoCountry(data.optString("iso_3166_1"));
            video.setKey(data.optString("key"));
            video.setName(data.optString("name"));
            video.setSite(data.optString("site"));
            video.setSize(data.optInt("size"));
            video.setType(VideoType.of(data.optString("type")));
        } catch (JSONException e) {
            Log.e(TAG, "parseVideo: Unable to parse video", e);
            video = null;
        }

        return video;
    }

    private static Date toDate(String value) {
        Date date = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            date = sdf.parse(value);
        } catch (Exception e) {
            Log.e(TAG, "toDate: A parsing exception has occurred", e);
        }

        return date;
    }

    private static List<Integer> toIntList(JSONArray array) {
        List<Integer> list = new ArrayList<>();

        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                list.add(array.optInt(i));
            }
        }

        return list;
    }
}
