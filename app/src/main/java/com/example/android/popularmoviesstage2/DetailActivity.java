package com.example.android.popularmoviesstage2;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesstage1.R;
import com.example.android.popularmoviesstage2.data.FetchMovie;
import com.example.android.popularmoviesstage2.data.Movie;
import com.example.android.popularmoviesstage2.data.Poster;
import com.example.android.popularmoviesstage2.utils.InternetCheck;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    public static final String POSTER = "poster";
    private static final String TAG = "DetailActivity";

    private Poster poster;

    private ImageView mPoster;
    private TextView mTitle;
    private TextView mOptionalTitle;
    private View mDivider;
    private TextView mRelease;
    private TextView mRunTime;
    private TextView mRating;
    private TextView mSynopsis;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, YYYY", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setupActionBar();

        Intent intent = getIntent();

        if (intent == null || !intent.hasExtra(POSTER)) {
            closeOnError();
            return;
        }

        poster = intent.getParcelableExtra(POSTER);

        if (poster == null) {
            closeOnError();
            return;
        }

        captureViews();
        fetchMovie();
    }

    private void captureViews() {
        mTitle = findViewById(R.id.tvDetailTitle);
        mOptionalTitle = findViewById(R.id.tvDetailOptionalTitle);
        mDivider = findViewById(R.id.divider);
        mPoster = findViewById(R.id.ivDetailPoster);
        mRelease =  findViewById(R.id.tvDetailRelease);
        mRunTime = findViewById(R.id.tvDetailRunTime);
        mRating = findViewById(R.id.tvDetailRating);
        mSynopsis = findViewById(R.id.tvDetailSynopsis);
    }

    private void closeOnError() {
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void fetchMovie() {
        mTitle.setText(poster.getTitle());

        new InternetCheck(new InternetCheck.Consumer() {
            @Override
            public void accept(boolean internet) {
                if (internet) {
                    Log.i(TAG, "accept: Has Internet");
                    new FetchMovie(new FetchMovie.Response() {
                        @Override
                        public void done(Movie movie) {
                            if (movie != null) {
                                populateUI(movie);
                            } else {
                                closeOnError();
                            }
                        }
                    }).execute(poster.getId());
                } else {
                    Log.i(TAG, "accept: Cannot Reach the Internet");
                    closeOnError();
                }
            }
        });
    }

    private void populateUI(Movie movie) {
        if (movie == null) {
            closeOnError();
            return;
        }

        mTitle.setText(coalesce(movie.getOriginalTitle(), movie.getTitle()));

        String path = coalesce(movie.getPosterPath(), movie.getBackdropPath());
        if (!path.isEmpty()) {
            Picasso.with(this)
                    .load(Poster.PREFIX + path)
                    .into(mPoster);
        } else {
            mPoster.setImageResource(R.drawable.tmdb_logo);
        }

        if (!coalesce(movie.getOriginalTitle(), movie.getTitle()).equals(
                coalesce(movie.getTitle(), movie.getOriginalTitle()))) {
            mOptionalTitle.setText(movie.getTitle());
            mOptionalTitle.setVisibility(View.VISIBLE);
            mDivider.setVisibility(View.VISIBLE);
        }

        if (movie.getReleaseDate() != null) {
            mRelease.setText(simpleDateFormat.format(movie.getReleaseDate()));
        }

        Resources res = getResources();

        int runtime = movie.getRunTime();
        mRunTime.setText(res.getQuantityString(R.plurals.runtime, runtime, runtime));

        int count = movie.getVoteCount();
        mRating.setText(res.getQuantityString(R.plurals.rating, count, count, movie.getVoteAverage()));

        mSynopsis.setText(coalesce(movie.getOverview()));
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private String coalesce(String ... values) {
        for (String value : values) {
            if (value != null) {
                return value;
            }
        }

        return "";
    }
}
