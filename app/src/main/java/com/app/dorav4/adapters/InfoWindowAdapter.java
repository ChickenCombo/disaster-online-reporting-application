package com.app.dorav4.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.dorav4.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;
    private Context mContext;

    public InfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.layout_marker, null);
    }

    private void renderWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        String snippet = marker.getSnippet();

        TextView tvMarkerTitle = view.findViewById(R.id.tvMarkerTitle);
        TextView tvMarkerSnippet = view.findViewById(R.id.tvMarkerSnippet);

        tvMarkerTitle.setText(title);
        tvMarkerSnippet.setText(snippet);
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
