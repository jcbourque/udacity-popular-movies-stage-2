package com.example.android.popularmoviesstage2.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.popularmoviesstage2.data.MovieDatabase;
import com.example.android.popularmoviesstage2.data.Poster;

import java.util.List;

public class PosterViewModel extends AndroidViewModel {
    private LiveData<List<Poster>> posters;

    public PosterViewModel(@NonNull Application application) {
        super(application);

        MovieDatabase db = MovieDatabase.getInstance(this.getApplication());
        posters = db.posterDao().getAll();
    }

    public LiveData<List<Poster>> getPosters() {
        return posters;
    }
}
