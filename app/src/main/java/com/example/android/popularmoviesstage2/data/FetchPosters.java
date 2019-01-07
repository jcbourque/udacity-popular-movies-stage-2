package com.example.android.popularmoviesstage2.data;

import android.os.AsyncTask;

import com.example.android.popularmoviesstage2.utils.MovieSort;
import com.example.android.popularmoviesstage2.utils.MovieUtils;

import java.util.ArrayList;
import java.util.List;

public class FetchPosters extends AsyncTask<MovieSort, Void, List<Poster>> {
    private final int page;
    private final Response response;
    private MovieSort sort = MovieSort.MOST_POPULAR;

    public FetchPosters(int page, Response response) {
        this.page = page;
        this.response = response;
    }

    @Override
    protected List<Poster> doInBackground(MovieSort... movieSorts) {
        List<Poster> posters = new ArrayList<>();

        if (!isCancelled()) {
            if (movieSorts.length > 0) {
                sort = movieSorts[0];
            }

            posters.addAll(MovieUtils.getPosters(sort, page));
        }

        return posters;
    }

    @Override
    protected void onPostExecute(List<Poster> posters) {
        response.done(posters);
    }

    public interface Response {
        void done(List<Poster> posters);
    }
}
