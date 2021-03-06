package com.example.android.popularmoviesstage2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesstage2.data.Movie;
import com.example.android.popularmoviesstage2.data.MovieDatabase;
import com.example.android.popularmoviesstage2.data.Poster;
import com.example.android.popularmoviesstage2.data.Review;
import com.example.android.popularmoviesstage2.data.Video;
import com.example.android.popularmoviesstage2.model.FavoriteViewModel;
import com.example.android.popularmoviesstage2.model.FavoriteViewModelFactory;
import com.example.android.popularmoviesstage2.utils.AppExecutors;
import com.example.android.popularmoviesstage2.utils.InternetCheck;
import com.example.android.popularmoviesstage2.utils.JsonUtils;
import com.example.android.popularmoviesstage2.utils.NetCallback;
import com.example.android.popularmoviesstage2.utils.NetUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static Bundle mBundleState;

    private MovieDatabase db;
    private Poster poster;
    private Movie movie;

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

    @Override
    public void onClick(View v) {
        String key = (String) v.getTag(R.string.tag_video_key);

        Uri uri = NetUtils.getYouTubeUri(key);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

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
        String posterIntentKey = getString(R.string.intent_poster);

        if (intent == null || !intent.hasExtra(posterIntentKey)) {
            closeOnError();
            return;
        }

        poster = intent.getParcelableExtra(posterIntentKey);

        if (poster == null) {
            closeOnError();
            return;
        }

        if (savedInstanceState != null) {
            String movieBundleKey = getString(R.string.bundle_movie);

            if (savedInstanceState.containsKey(movieBundleKey)) {
                movie = savedInstanceState.getParcelable(movieBundleKey);

                if (movie != null && movie.getId() != poster.getId()) {
                    movie = null;
                }
            }
        }

        captureViews();
        fetchMovie();
        observeFavorite();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (movie != null) {
            mBundleState = new Bundle();
            mBundleState.putParcelable(getString(R.string.bundle_movie), movie);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        String movieBundleKey = getString(R.string.bundle_movie);

        if (mBundleState != null && mBundleState.containsKey(movieBundleKey)) {
            movie = mBundleState.getParcelable(movieBundleKey);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String movieBundleKey = getString(R.string.bundle_movie);

        if (savedInstanceState.containsKey(movieBundleKey)) {
            movie = savedInstanceState.getParcelable(movieBundleKey);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (movie != null) {
            outState.putParcelable(getString(R.string.bundle_movie), movie);
        }
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
        if (movie != null) {
            populateUI();
            return;
        }

        mTitle.setText(poster.getTitle());

        new InternetCheck(new InternetCheck.Consumer() {
            @Override
            public void accept(boolean internet) {
                if (internet) {
                    NetUtils.getResponseFromHttpUrl(NetUtils.getMovieURL(poster.getId()), new NetCallback() {
                        @Override
                        public void done(final String response) {
                            movie = JsonUtils.parseMovie(response);

                            if (movie != null) {
                                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        populateUI();
                                    }
                                });
                            }
                        }

                        @Override
                        public void error(Exception e) {
                            closeOnError();
                        }
                    });
                } else {
                    closeOnError();
                }
            }
        });
    }

    private void fetchReviews() {
        if (movie != null && movie.hasReviews()) {
            populateReviews();
            return;
        }

        new InternetCheck(new InternetCheck.Consumer() {
            @Override
            public void accept(boolean internet) {
                if (internet) {
                    NetUtils.getResponseFromHttpUrl(NetUtils.getReviewsURL(poster.getId()), new NetCallback() {
                        @Override
                        public void done(String response) {
                            movie.setReviews(JsonUtils.parseReviews(response));
                            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    populateReviews();
                                }
                            });
                        }

                        @Override
                        public void error(Exception e) {
                            Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void fetchVideos() {
        if (movie != null && movie.hasVideos()) {
            populateVideos();
            return;
        }

        new InternetCheck(new InternetCheck.Consumer() {
            @Override
            public void accept(boolean internet) {
                if (internet) {
                    NetUtils.getResponseFromHttpUrl(NetUtils.getVideosURL(poster.getId()), new NetCallback() {
                        @Override
                        public void done(final String response) {
                            movie.setVideos(JsonUtils.parseVideos(response));
                            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    populateVideos();
                                }
                            });
                        }

                        @Override
                        public void error(Exception e) {
                            Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
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

    private void populateReviews() {
        if (movie != null && movie.hasReviews()) {
            LinearLayout parent = findViewById(R.id.llContent);

            LinearLayout sectionHeader = (LinearLayout) getLayoutInflater().inflate(R.layout.section_header, null);

            TextView title = sectionHeader.findViewById(R.id.tvSectionHeaderTitle);
            title.setText(R.string.section_header_reviews);

            parent.addView(sectionHeader);

            for (Review review : movie.getReviews()) {
                LinearLayout reviewLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.reviews, null);

                TextView tvAuthor = reviewLayout.findViewById(R.id.tvAuthor);
                tvAuthor.setText(review.getAuthor());

                TextView tvContent = reviewLayout.findViewById(R.id.tvContent);
                tvContent.setText(review.getContent());

                parent.addView(reviewLayout);
            }
        }
    }

    private void populateUI() {
        if (movie == null) {
            closeOnError();
            return;
        }

        fetchVideos();

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

    private void populateVideos() {
        if (movie != null && movie.hasVideos()) {
            LinearLayout parent = findViewById(R.id.llContent);

            LinearLayout sectionHeader = (LinearLayout) getLayoutInflater().inflate(R.layout.section_header, null);

            TextView title = sectionHeader.findViewById(R.id.tvSectionHeaderTitle);
            title.setText(R.string.section_header_videos);

            parent.addView(sectionHeader);

            for (Video video : movie.getVideos()) {
                if (video.getSite().equalsIgnoreCase(getString(R.string.youtube))) {
                    LinearLayout videoLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.videos, null);

                    ImageView ivPlay = videoLayout.findViewById(R.id.ivVideoPlay);
                    ivPlay.setTag(R.string.tag_video_key, video.getKey());
                    ivPlay.setOnClickListener(this);

                    TextView tvName = videoLayout.findViewById(R.id.tvVideoName);
                    tvName.setText(video.getName());

                    parent.addView(videoLayout);
                }
            }
        }

        fetchReviews();
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
