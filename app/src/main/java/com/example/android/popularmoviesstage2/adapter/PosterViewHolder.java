package com.example.android.popularmoviesstage2.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    final private ImageView mPoster;
    final private PosterClickListener mPosterClickListener;

    public PosterViewHolder(@NonNull View itemView, @NonNull ImageView poster,
                            PosterClickListener posterClickListener) {
        super(itemView);

        itemView.setOnClickListener(this);
        mPoster = poster;
        mPosterClickListener = posterClickListener;
    }

    public ImageView getPosterView() {
        return mPoster;
    }

    @Override
    public void onClick(View view) {
        if (mPosterClickListener != null) {
            mPosterClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
