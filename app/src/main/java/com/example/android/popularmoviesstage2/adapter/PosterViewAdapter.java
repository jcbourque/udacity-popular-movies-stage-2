package com.example.android.popularmoviesstage2.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.data.MovieDatabase;
import com.example.android.popularmoviesstage2.data.Poster;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PosterViewAdapter extends ListAdapter<Poster, PosterViewHolder> {
    private static final String TAG = "PosterViewAdapter";
    private final List<Poster> mPosters = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;
    private PosterClickListener mPosterClickListener;

    private static final DiffUtil.ItemCallback<Poster> DIFF_CALLBACK = new DiffUtil.ItemCallback<Poster>() {
        @Override
        public boolean areItemsTheSame(@NonNull Poster poster, @NonNull Poster t1) {
            return poster.getId() == t1.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Poster poster, @NonNull Poster t1) {
            return poster.getPath().equals(t1.getPath());
        }
    };

    public PosterViewAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void addPosters(List<Poster> posters) {
        if (!mPosters.containsAll(posters)) {
            mPosters.addAll(posters);
        }

        if (!posters.isEmpty()) {
            submitList(new ArrayList<>(mPosters));
            notifyDataSetChanged();
        }
    }

    public Poster getPoster(int position) {
        return getItem(position);
    }

    @Override
    @NonNull
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.poster_view_item, parent, false);
        ImageView poster = view.findViewById(R.id.ivDetailPoster);
        return new PosterViewHolder(view, poster, mPosterClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final PosterViewHolder viewHolder, int i) {
        Poster poster = getItem(i);

        if (poster != null) {
            if (poster.getPath() != null) {
                Picasso.with(mContext)
                        .load(poster.getFullPath())
                        .into(viewHolder.getPosterView());
            } else {
                viewHolder.getPosterView().setImageResource(R.drawable.tmdb_logo);
            }

            final ImageView favorite = viewHolder.getPosterView().getRootView().findViewById(R.id.ivFavorite);
            favorite.setTag(R.string.tag_position_key, i);

            LiveData<Boolean> fav = MovieDatabase.getInstance(mContext).posterDao().isFavorite(poster.getId());
            fav.observe((LifecycleOwner) mContext, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    int resourceId;

                    if (aBoolean != null && aBoolean) {
                        resourceId = R.drawable.ic_fav;
                    } else {
                        resourceId = R.drawable.ic_fav_empty;
                    }

                    favorite.setImageResource(resourceId);
                    favorite.setTag(R.string.tag_resource_key, resourceId);
                }
            });
        }
    }

    public void restoreState(List<Parcelable> data) {
        List<Poster> posters = new ArrayList<>();

        for (Parcelable p : data) {
            if (p instanceof Poster) {
                posters.add((Poster) p);
            }
        }

        setData(posters);
    }

    public ArrayList<Parcelable> saveState() {
        ArrayList<Parcelable> list = new ArrayList<>();
        list.addAll(mPosters);
        return list;
    }

    public void clearData() {
        mPosters.clear();
        submitList(mPosters);
        notifyDataSetChanged();
    }

    public void setData(List<Poster> data) {
        if (data != null) {
            mPosters.clear();
            addPosters(data);
        }
    }

    public void setPosterClickListener(PosterClickListener posterClickListener) {
        mPosterClickListener = posterClickListener;
    }
}
