package com.example.android.popularmoviesstage1.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesstage1.R;
import com.example.android.popularmoviesstage1.data.Poster;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PosterViewAdapter extends ListAdapter<Poster, PosterViewAdapter.ViewHolder> {
    private final List<Poster> mPosters = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.poster_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Poster poster = getItem(i);

        if (poster != null && poster.getPath() != null) {
            Picasso.with(mContext)
                    .load(poster.getFullPath())
                    .into(viewHolder.mPoster);
        } else {
            viewHolder.mPoster.setImageResource(R.drawable.tmdb_logo);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mPoster;

        ViewHolder(View itemView) {
            super(itemView);
            mPoster = itemView.findViewById(R.id.ivPoster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
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

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
