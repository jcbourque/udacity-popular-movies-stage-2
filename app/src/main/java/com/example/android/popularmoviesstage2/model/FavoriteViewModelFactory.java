package com.example.android.popularmoviesstage2.model;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.popularmoviesstage2.data.MovieDatabase;

public class FavoriteViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final MovieDatabase db;
    private final int id;

    public FavoriteViewModelFactory(MovieDatabase db, int id) {
        this.db = db;
        this.id = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FavoriteViewModel(db, id);
    }
}
