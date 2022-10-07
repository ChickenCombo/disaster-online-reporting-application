package com.app.dorav4.models;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Markers implements ClusterItem {
    private final LatLng position;
    private final double distance;
    private final String title;
    private final String snippet;

    public Markers(double lat, double lng, String title, String snippet, double distance) {
        position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
        this.distance = distance;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    @NonNull
    @Override
    public String getTitle() {
        return title;
    }

    @NonNull
    @Override
    public String getSnippet() {
        return snippet;
    }

    @NonNull
    public double getDistance() {
        return distance;
    }
}
