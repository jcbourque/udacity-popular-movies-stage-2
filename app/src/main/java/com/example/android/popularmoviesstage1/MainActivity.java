package com.example.android.popularmoviesstage1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesstage1.adapter.EndlessRecyclerViewScrollListener;
import com.example.android.popularmoviesstage1.adapter.PosterClickListener;
import com.example.android.popularmoviesstage1.adapter.PosterViewAdapter;
import com.example.android.popularmoviesstage1.data.FetchPosters;
import com.example.android.popularmoviesstage1.data.Poster;
import com.example.android.popularmoviesstage1.utils.DisplayUtils;
import com.example.android.popularmoviesstage1.utils.InternetCheck;
import com.example.android.popularmoviesstage1.utils.MovieSort;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PosterClickListener,
        EndlessRecyclerViewScrollListener.LoadHandler {
    private static final String TAG = "MainActivity";
    private static Bundle mBundleState;
    private final String LAYOUT_MANAGER_KEY = "layoutManagerState";
    private final String PAGE_KEY = "pageState";
    private final String RECYCLER_DATA_KEY = "recyclerData";
    private final String RECYCLER_POSITION_KEY = "recyclerPositionState";
    private final String SCROLL_LISTENER_KEY = "scrollListenerState";
    private final String SORT_KEY = "sortKey";
    private PosterViewAdapter adapter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private MenuItem mMostPopularMenuItem;
    private MenuItem mTopRatedMenuItem;
    private int page;
    private MovieSort currentSort = MovieSort.MOST_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        if (savedInstanceState == null || savedInstanceState.getInt(PAGE_KEY) == 0) {
            loadMore();
        }

        Toast.makeText(this, R.string.powered_by, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort, menu);

        if (mMostPopularMenuItem == null) {
            mMostPopularMenuItem = menu.findItem(R.id.action_fetch_popular);
        }

        if (mTopRatedMenuItem == null) {
            mTopRatedMenuItem = menu.findItem(R.id.action_fetch_top_rated);
        }

        switch (currentSort) {
            case MOST_POPULAR:
                mTopRatedMenuItem.setVisible(true);
                setTitle(R.string.popular_title);
                break;
            case TOP_RATED:
                mMostPopularMenuItem.setVisible(true);
                setTitle(R.string.top_rated_title);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_fetch_popular:
                currentSort = MovieSort.MOST_POPULAR;
                mMostPopularMenuItem.setVisible(false);
                mTopRatedMenuItem.setVisible(true);
                setTitle(R.string.popular_title);
                break;
            case R.id.action_fetch_top_rated:
                currentSort = MovieSort.TOP_RATED;
                mTopRatedMenuItem.setVisible(false);
                mMostPopularMenuItem.setVisible(true);
                setTitle(R.string.top_rated_title);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        page = 0;
        adapter.clearData();
        loadMore();
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        Poster poster = adapter.getPoster(position);
        intent.putExtra(DetailActivity.POSTER, poster);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBundleState = new Bundle();
        mBundleState.putInt(PAGE_KEY, page);
        mBundleState.putInt(SORT_KEY, currentSort.ordinal());
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

            if (mBundleState.containsKey(SORT_KEY)) {
                currentSort = MovieSort.values()[mBundleState.getInt(SORT_KEY)];
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

        if (savedInstanceState.containsKey(SORT_KEY)) {
            currentSort = MovieSort.values()[savedInstanceState.getInt(SORT_KEY)];
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
        outState.putInt(SORT_KEY, currentSort.ordinal());
        outState.putInt(SCROLL_LISTENER_KEY, mScrollListener.saveState());
        outState.putParcelableArrayList(RECYCLER_DATA_KEY, adapter.saveState());
        outState.putInt(RECYCLER_POSITION_KEY, mGridLayoutManager.findFirstCompletelyVisibleItemPosition());
        outState.putParcelable(LAYOUT_MANAGER_KEY, mGridLayoutManager.onSaveInstanceState());
    }

    private void captureReferences() {
        mRecyclerView = findViewById(R.id.rvPosters);
        mErrorMessage = findViewById(R.id.tvErrorMessage);
        mLoadingIndicator = findViewById(R.id.pbLoadingIndicator);
    }

    private void showData() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showError() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadMore() {
        new InternetCheck(new InternetCheck.Consumer() {
            @Override
            public void accept(boolean internet) {
                if (internet) {
                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    new FetchPosters(++page, new FetchPosters.Response() {
                        @Override
                        public void done(List<Poster> posters) {
                            mLoadingIndicator.setVisibility(View.INVISIBLE);

                            if (posters != null) {
                                adapter.addPosters(posters);
                                showData();
                            } else {
                                showError();
                            }
                        }
                    }).execute(currentSort);
                } else {
                    showError();
                }
            }
        });
    }
}
