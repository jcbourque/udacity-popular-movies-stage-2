package com.example.android.popularmoviesstage2.data;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.example.android.popularmoviesstage2.adapter.PosterViewAdapter;

import java.util.List;

public class PosterObserver implements Observer<List<Poster>> {
    final PosterViewAdapter adapter;
    final ShowData showData;

    public PosterObserver(PosterViewAdapter adapter, ShowData showData) {
        this.adapter = adapter;
        this.showData = showData;
    }

    @Override
    public void onChanged(@Nullable List<Poster> posters) {
        if (posters.isEmpty()) {
            adapter.clearData();
            showData.showEmpty();
        } else {
            adapter.setData(posters);
            showData.showData();
        }
    }
}
