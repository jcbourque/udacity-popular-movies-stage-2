package com.example.android.popularmoviesstage2.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PosterDao {
    @Query("SELECT * FROM fav")
    LiveData<List<Poster>> getAll();

    @Query("SELECT 1 FROM fav WHERE id = :id")
    LiveData<Boolean> isFavorite(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void like(Poster poster);

    @Delete
    void unlike(Poster poster);
}
