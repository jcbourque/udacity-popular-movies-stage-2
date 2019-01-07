package com.example.android.popularmoviesstage2.data;

import android.os.AsyncTask;

import com.example.android.popularmoviesstage2.utils.MovieUtils;

public class FetchMovie extends AsyncTask<Integer, Void, Movie> {
    private final Response response;

    public FetchMovie(Response response) {
        this.response = response;
    }

    @Override
    protected Movie doInBackground(Integer... integers) {
        Movie movie = null;

        if (!isCancelled()) {
            if (integers.length > 0) {
                movie = MovieUtils.getMovie(integers[0]);
            }
        }

        return movie;
    }

    @Override
    protected void onPostExecute(Movie movie) {
        response.done(movie);
    }

    public interface Response {
        void done(Movie movie);
    }
}
