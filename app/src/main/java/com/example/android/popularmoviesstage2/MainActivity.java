package com.example.android.popularmoviesstage2;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesstage2.adapter.EndlessRecyclerViewScrollListener;
import com.example.android.popularmoviesstage2.adapter.PosterClickListener;
import com.example.android.popularmoviesstage2.adapter.PosterViewAdapter;
import com.example.android.popularmoviesstage2.data.MovieDatabase;
import com.example.android.popularmoviesstage2.data.Poster;
import com.example.android.popularmoviesstage2.data.PosterObserver;
import com.example.android.popularmoviesstage2.data.ShowData;
import com.example.android.popularmoviesstage2.model.PosterViewModel;
import com.example.android.popularmoviesstage2.utils.AppExecutors;
import com.example.android.popularmoviesstage2.utils.DisplayUtils;
import com.example.android.popularmoviesstage2.utils.InternetCheck;
import com.example.android.popularmoviesstage2.utils.JsonUtils;
import com.example.android.popularmoviesstage2.utils.MovieSort;
import com.example.android.popularmoviesstage2.utils.NetCallback;
import com.example.android.popularmoviesstage2.utils.NetUtils;

public class MainActivity extends AppCompatActivity implements PosterClickListener,
        EndlessRecyclerViewScrollListener.LoadHandler, ShowData,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static Bundle mBundleState;
    private final String LAYOUT_MANAGER_KEY = "layoutManagerState";
    private final String PAGE_KEY = "pageState";
    private final String RECYCLER_DATA_KEY = "recyclerData";
    private final String RECYCLER_POSITION_KEY = "recyclerPositionState";
    private final String SCROLL_LISTENER_KEY = "scrollListenerState";
    private PosterViewAdapter adapter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private TextView mEmptyMessage;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private PosterViewModel posterViewModel;
    private PosterObserver posterObserver;
    private int page;
    private MovieDatabase db;

    @Override
    public void loadMore() {
        final String pref = getListPreference();

        if (pref.equals(getString(R.string.pref_list_type_value_favorites))) {
            setTitle(R.string.favorite_title);

            if (posterViewModel.getPosters().hasObservers()) {
                adapter.setData(posterViewModel.getPosters().getValue());
            } else {
                posterViewModel.getPosters().observe(this, posterObserver);
            }

            return;
        }

        posterViewModel.getPosters().removeObserver(posterObserver);

        final MovieSort sort;

        if (pref.equals(getString(R.string.pref_list_type_value_popular))) {
            setTitle(R.string.popular_title);
            sort = MovieSort.MOST_POPULAR;
        } else if (pref.equals(getString(R.string.pref_list_type_value_top_rated))) {
            setTitle(R.string.top_rated_title);
            sort = MovieSort.TOP_RATED;
        } else {
            Toast.makeText(this,R.string.unknown_preference, Toast.LENGTH_LONG).show();
            return;
        }

        new InternetCheck(new InternetCheck.Consumer() {
            @Override
            public void accept(boolean internet) {
                if (internet) {
                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    NetUtils.getResponseFromHttpUrl(NetUtils.getPostersURL(sort, ++page), new NetCallback() {
                        @Override
                        public void done(final String response) {
                            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.addPosters(JsonUtils.parsePosters(response));
                                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                                    showData();
                                }
                            });
                        }

                        @Override
                        public void error(Exception e) {
                            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                                    showError();
                                }
                            });
                        }
                    });
                } else {
                    showError();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void onFavorite(View view) {
        final Integer position = (Integer) view.getTag(R.string.tag_position_key);
        final Integer tag = (Integer) view.getTag(R.string.tag_resource_key);
        final Poster poster = adapter.getPoster(position == null ? 0 : position);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (tag != null && tag == R.drawable.ic_fav) {
                    db.posterDao().unlike(poster);
                } else {
                    db.posterDao().like(poster);
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        Poster poster = adapter.getPoster(position);
        intent.putExtra(getString(R.string.intent_poster), poster);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.action_settings) {
            return super.onOptionsItemSelected(item);
        }

        startActivity(new Intent(this, SettingsActivity.class));
        resetAdapterData();

        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        resetAdapterData();
        loadMore();
    }

    @Override
    public void showData() {
        mEmptyMessage.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mEmptyMessage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = MovieDatabase.getInstance(getApplicationContext());

        setupViewModel();
        setContentView(R.layout.activity_main);

        captureReferences();

        int numberOfColumns = DisplayUtils.calculateNoOfColumns(this, 185);

        adapter = new PosterViewAdapter(this);
        adapter.setPosterClickListener(this);

        mGridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        mScrollListener = new EndlessRecyclerViewScrollListener(mGridLayoutManager);

        mScrollListener.setLoadHandler(this);

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(mScrollListener);

        setupObserver();

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        loadMore();

        Toast.makeText(this, R.string.powered_by, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBundleState = new Bundle();
        mBundleState.putInt(PAGE_KEY, page);
        mBundleState.putParcelableArrayList(RECYCLER_DATA_KEY, adapter.saveState());
        mBundleState.putParcelable(LAYOUT_MANAGER_KEY, mGridLayoutManager.onSaveInstanceState());
        mBundleState.putInt(SCROLL_LISTENER_KEY, mScrollListener.saveState());
        mBundleState.putInt(RECYCLER_POSITION_KEY,
                mGridLayoutManager.findFirstCompletelyVisibleItemPosition());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (mBundleState != null) {
            if (mBundleState.containsKey(PAGE_KEY)) {
                page = mBundleState.getInt(PAGE_KEY);
            }

            if (adapter != null && mBundleState.containsKey(RECYCLER_DATA_KEY)) {
                adapter.restoreState(mBundleState.getParcelableArrayList(RECYCLER_DATA_KEY));
            }

            if (mGridLayoutManager != null) {
                Parcelable layoutManagerState = mBundleState.getParcelable(LAYOUT_MANAGER_KEY);

                if (layoutManagerState != null) {
                    mGridLayoutManager.onRestoreInstanceState(layoutManagerState);
                }
            }

            if (mScrollListener != null && mBundleState.containsKey(SCROLL_LISTENER_KEY)) {
                mScrollListener.restoreState(mBundleState.getInt(SCROLL_LISTENER_KEY));
            }

            if (mRecyclerView != null) {
                int position = mBundleState.getInt(RECYCLER_POSITION_KEY);

                if (position == RecyclerView.NO_POSITION) {
                    position = 0;
                }

                mRecyclerView.smoothScrollToPosition(position);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(PAGE_KEY)) {
            page = savedInstanceState.getInt(PAGE_KEY);
        }

        if (mScrollListener != null && savedInstanceState.containsKey(SCROLL_LISTENER_KEY)) {
            mScrollListener.restoreState(savedInstanceState.getInt(SCROLL_LISTENER_KEY));
        }

        if (adapter != null && savedInstanceState.containsKey(RECYCLER_DATA_KEY)) {
            adapter.restoreState(savedInstanceState.getParcelableArrayList(RECYCLER_DATA_KEY));
        }

        if (mGridLayoutManager != null && savedInstanceState.containsKey(LAYOUT_MANAGER_KEY)) {
            Parcelable layoutManagerState = savedInstanceState.getParcelable(LAYOUT_MANAGER_KEY);

            if (layoutManagerState != null) {
                mGridLayoutManager.onRestoreInstanceState(layoutManagerState);
            }
        }

        if (mRecyclerView != null && savedInstanceState.containsKey(RECYCLER_POSITION_KEY)) {
            int position = savedInstanceState.getInt(RECYCLER_POSITION_KEY);

            if (position == RecyclerView.NO_POSITION) {
                position = 0;
            }

            mRecyclerView.smoothScrollToPosition(position);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(PAGE_KEY, page);
        outState.putInt(SCROLL_LISTENER_KEY, mScrollListener.saveState());
        outState.putParcelableArrayList(RECYCLER_DATA_KEY, adapter.saveState());
        outState.putInt(RECYCLER_POSITION_KEY, mGridLayoutManager.findFirstCompletelyVisibleItemPosition());
        outState.putParcelable(LAYOUT_MANAGER_KEY, mGridLayoutManager.onSaveInstanceState());
    }

    private void captureReferences() {
        mRecyclerView = findViewById(R.id.rvPosters);
        mEmptyMessage = findViewById(R.id.tvEmptyMessage);
        mErrorMessage = findViewById(R.id.tvErrorMessage);
        mLoadingIndicator = findViewById(R.id.pbLoadingIndicator);
    }

    private String getListPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(getString(R.string.pref_list_type_value),
                getString(R.string.pref_list_type_value_popular));
    }

    private void resetAdapterData() {
        adapter.clearData();
        page = 0;
        mScrollListener.reset();
    }

    private void setupObserver() {
        posterObserver = new PosterObserver(adapter, this);
    }

    private void setupViewModel() {
        posterViewModel = ViewModelProviders.of(this).get(PosterViewModel.class);
    }

    private void showError() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mEmptyMessage.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }
}
