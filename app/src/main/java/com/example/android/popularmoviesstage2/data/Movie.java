package com.example.android.popularmoviesstage2.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Movie implements Parcelable {
    private int voteCount;
    private int id;
    private boolean video;
    private double voteAverage;
    private String title;
    private double popularity;
    private String posterPath;
    private String originalLanguage;
    private String originalTitle;
    private List<Integer> genreIds;
    private String backdropPath;
    private boolean adult;
    private String overview;
    private Date releaseDate;
    private int runTime;
    private List<Video> videos;
    private List<Review> reviews;

    public Movie() {}

    protected Movie(Parcel in) {
        voteCount = in.readInt();
        id = in.readInt();
        video = in.readInt() == 1;
        voteAverage = in.readDouble();
        title = in.readString();
        popularity = in.readDouble();
        posterPath = in.readString();
        originalLanguage = in.readString();
        originalTitle = in.readString();
        backdropPath = in.readString();
        adult = in.readInt() == 1;
        overview = in.readString();
        long releaseTime = in.readLong();
        releaseDate = releaseTime == 0 ? null : new Date(releaseTime);
        runTime = in.readInt();
        videos = in.createTypedArrayList(Video.CREATOR);
        reviews = in.createTypedArrayList(Review.CREATOR);

        int size = in.readInt();
        if (size > 0) {
            genreIds = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                genreIds.add(in.readInt());
            }
        } else {
            genreIds = null;
        }
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public boolean hasGenreIds() {
        return genreIds != null && !genreIds.isEmpty();
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public boolean hasVideos() {
        return videos != null && !videos.isEmpty();
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public boolean hasReviews() {
        return reviews != null && !reviews.isEmpty();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(voteCount);
        dest.writeInt(id);
        dest.writeInt(video ? 1 : 0);
        dest.writeDouble(voteAverage);
        dest.writeString(title);
        dest.writeDouble(popularity);
        dest.writeString(posterPath);
        dest.writeString(originalLanguage);
        dest.writeString(originalTitle);
        dest.writeString(backdropPath);
        dest.writeInt(adult ? 1 : 0);
        dest.writeString(overview);
        dest.writeLong(releaseDate == null ? 0 : releaseDate.getTime());
        dest.writeInt(runTime);
        dest.writeTypedList(videos);
        dest.writeTypedList(reviews);

        if (hasGenreIds()) {
            dest.writeInt(genreIds.size());
            for (Integer genreId : genreIds) {
                dest.writeInt(genreId);
            }
        } else {
            dest.writeInt(0);
        }
    }
}
