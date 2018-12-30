package com.example.android.popularmoviesstage1.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Poster implements Parcelable {
    public static final String PREFIX = "http://image.tmdb.org/t/p/w185";

    private int id;
    private String path;
    private String title;

    public Poster() {}

    protected Poster(Parcel in) {
        id = in.readInt();
        path = in.readString();
        title = in.readString();
    }

    public static final Creator<Poster> CREATOR = new Creator<Poster>() {
        @Override
        public Poster createFromParcel(Parcel in) {
            return new Poster(in);
        }

        @Override
        public Poster[] newArray(int size) {
            return new Poster[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullPath() {
        return PREFIX + path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(path);
        dest.writeString(title);
    }
}
