package com.example.android.popularmoviesstage1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.android.popularmoviesstage1.adapter.PosterViewAdapter;
import com.example.android.popularmoviesstage1.utils.DisplayUtils;

public class MainActivity extends AppCompatActivity implements PosterViewAdapter.ItemClickListener {
    private static final String TAG = "MainActivity";
    PosterViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: " + BuildConfig.MOVIE_DB_API_KEY);

        // data to populate the RecyclerView with
        String[] data = new String[100];
        for (int i = 0; i < 100; i++) {
            data[i] = "" + (i + 1);
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvPosters);
        int numberOfColumns = DisplayUtils.calculateNoOfColumns(this, 150);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setHasFixedSize(true);
        adapter = new PosterViewAdapter(this, data);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i(TAG, "You clicked number " + adapter.getItem(position) + ", which is at cell position " + position);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("", "");
        startActivity(new Intent(this, DetailActivity.class));
    }
}
