package com.example.android.popularmoviesstage2.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmoviesstage2.data.MovieDatabase;

public class FavoriteViewModel extends ViewModel {
    private LiveData<Boolean> favorite;

    public FavoriteViewModel(MovieDatabase db, int id) {
        favorite = db.posterDao().isFavorite(id);
    }

    public LiveData<Boolean> isFavorite() {
        return favorite;
    }
}
