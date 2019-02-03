package com.example.android.popularmoviesstage2.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {
    private String id;
    private String isoLanguage;
    private String isoCountry;
    private String key;
    private String name;
    private String site;
    private int size;
    private VideoType type;

    public Video() {}

    protected Video(Parcel in) {
        id = in.readString();
        isoLanguage = in.readString();
        isoCountry = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
        type = VideoType.of(in.readString());
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsoLanguage() {
        return isoLanguage;
    }

    public void setIsoLanguage(String isoLanguage) {
        this.isoLanguage = isoLanguage;
    }

    public String getIsoCountry() {
        return isoCountry;
    }

    public void setIsoCountry(String isoCountry) {
        this.isoCountry = isoCountry;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public VideoType getType() {
        return type;
    }

    public void setType(VideoType type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(isoLanguage);
        dest.writeString(isoCountry);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeInt(size);
        dest.writeString(type == null ? VideoType.Unknown.name() : type.name());
    }
}
