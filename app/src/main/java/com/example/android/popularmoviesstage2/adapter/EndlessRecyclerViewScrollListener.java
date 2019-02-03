package com.example.android.popularmoviesstage2.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private int visibleThreshold;
    private int previousTotalItemCount;
    private boolean loading = true;

    private GridLayoutManager layoutManager;
    private LoadHandler loadHandler;

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        visibleThreshold = layoutManager.getSpanCount() * 3;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView view, int dx, int dy) {
        int totalItemCount = layoutManager.getItemCount();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

        if (totalItemCount < previousTotalItemCount) {
            previousTotalItemCount = totalItemCount;

            if (totalItemCount == 0) {
                loading = true;
                layoutManager.scrollToPosition(0);
            }
        }

        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        if (!loading && (lastVisibleItemPosition + visibleThreshold) >= totalItemCount) {
            loading = true;
            loadMore();
        }
    }

    public void reset() {
        loading = false;
        previousTotalItemCount = 0;
    }

    public void restoreState(String state) {
        String[] tokens = state.split("~");

        int position = -1, itemCount = -1;

        if (tokens.length == 2) {
            try {
                position = Integer.parseInt(tokens[0]);
            } catch (Exception ignore) {}
            try {
                itemCount = Integer.parseInt(tokens[1]);
            } catch (Exception ignore) {}
        }

        if (position > -1) {
            layoutManager.scrollToPosition(position);
        }

        if (itemCount > -1) {
            previousTotalItemCount = itemCount;
        }
    }

    public String saveState() {
        return layoutManager.findFirstVisibleItemPosition() + "~" + previousTotalItemCount;
    }

    public void setLoadHandler(LoadHandler loadHandler) {
        this.loadHandler = loadHandler;
    }

    private void loadMore() {
        if (loadHandler != null) {
            loadHandler.loadMore();
        }
    }

    public interface LoadHandler {
        void loadMore();
    }
}