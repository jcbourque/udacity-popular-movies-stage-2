package com.example.android.popularmoviesstage2.data;

public enum VideoType {
    Trailer,
    Teaser,
    Clip,
    Featurette,
    Unknown;

    public static VideoType of(String name) {
        for (VideoType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return Unknown;
    }
}
