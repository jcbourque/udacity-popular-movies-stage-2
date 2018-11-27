package com.example.android.popularmoviesstage1.model;

import java.util.ArrayList;
import java.util.List;

public class Result {
    /*
      "page": 1,
  "total_results": 389820,
  "total_pages": 19491,
  "results": [
     */

    private int page;
    private int totalResults;
    private int totalPages;
    private List<Movie> movies;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public void addMovie(Movie movie) {
        if (movies == null) {
            movies = new ArrayList<>();
        }

        movies.add(movie);
    }
}
