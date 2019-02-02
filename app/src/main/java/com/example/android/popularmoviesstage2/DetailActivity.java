package com.example.android.popularmoviesstage2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesstage2.data.FetchMovie;
import com.example.android.popularmoviesstage2.data.Movie;
import com.example.android.popularmoviesstage2.data.MovieDatabase;
import com.example.android.popularmoviesstage2.data.Poster;
import com.example.android.popularmoviesstage2.model.FavoriteViewModel;
import com.example.android.popularmoviesstage2.model.FavoriteViewModelFactory;
import com.example.android.popularmoviesstage2.utils.AppExecutors;
import com.example.android.popularmoviesstage2.utils.InternetCheck;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    public static final String POSTER = "poster";
    private static final String TAG = "DetailActivity";

    private MovieDatabase db;

    private Poster poster;

    private ImageView mPoster;
    private ImageView mFavorite;
    private TextView mTitle;
    private TextView mOptionalTitle;
    private View mDivider;
    private TextView mRelease;
    private TextView mRunTime;
    private TextView mRating;
    private TextView mSynopsis;

    private SimpleDateFormat simpleDateFormat;

    public void onFavorite(View view) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Integer tag = (Integer) mFavorite.getTag(R.string.tag_resource_key);

                if (tag != null && tag == R.drawable.ic_fav) {
                    db.posterDao().unlike(poster);
                } else {
                    db.posterDao().like(poster);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);
        db = MovieDatabase.getInstance(getApplicationContext());

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
        observeFavorite();
    }

    private void captureViews() {
        mTitle = findViewById(R.id.tvDetailTitle);
        mOptionalTitle = findViewById(R.id.tvDetailOptionalTitle);
        mDivider = findViewById(R.id.divider);
        mPoster = findViewById(R.id.ivDetailPoster);
        mFavorite = findViewById(R.id.ivFavorite);
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
                    closeOnError();
                }
            }
        });
    }

    private void observeFavorite() {
        FavoriteViewModelFactory factory = new FavoriteViewModelFactory(db, poster.getId());
        FavoriteViewModel viewModel = ViewModelProviders.of(this, factory).get(FavoriteViewModel.class);

        viewModel.isFavorite().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                int resourceId;

                if (aBoolean != null && aBoolean) {
                    resourceId = R.drawable.ic_fav;
                } else {
                    resourceId = R.drawable.ic_fav_empty;
                }

                mFavorite.setImageResource(resourceId);
                mFavorite.setTag(R.string.tag_resource_key, resourceId);
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
