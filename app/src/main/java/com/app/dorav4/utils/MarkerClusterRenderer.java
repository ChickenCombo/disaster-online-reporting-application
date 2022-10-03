package com.app.dorav4.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.app.dorav4.R;
import com.app.dorav4.models.Markers;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class MarkerClusterRenderer extends DefaultClusterRenderer<Markers> {
    Context context;

    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<Markers> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    // Change the default icon with the custom marker
    @Override
    protected void onBeforeClusterItemRendered(@NonNull Markers item, @NonNull MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        markerOptions.icon(bitmapDescriptor(context, getIconId(item.getTitle())));
    }

    // Get the custom marker's icon based on the disaster type
    private int getIconId(String disasterType) {
        switch (disasterType) {
            case "Typhoon":
                return R.drawable.ic_map_typhoon;
            case "Heavy Rain":
                return R.drawable.ic_map_heavy_rain;
            case "Landslide":
                return R.drawable.ic_map_landslide;
            case "Earthquake":
                return R.drawable.ic_map_earthquake;
            case "Fire":
                return R.drawable.ic_map_fire;
            case "Flood":
                return R.drawable.ic_map_flood;
            case "Tsunami":
                return R.drawable.ic_map_tsunami;
            case "Volcanic Eruption":
                return R.drawable.ic_map_volcanic_eruption;
            default:
                return R.drawable.ic_map_evacuate;
        }
    }

    // Convert icon drawables into a Bitmap
    private BitmapDescriptor bitmapDescriptor (Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
