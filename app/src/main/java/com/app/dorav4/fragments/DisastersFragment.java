package com.app.dorav4.fragments;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.dorav4.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class DisastersFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    LocationRequest locationRequest;

    DatabaseReference reportsReference;

    List<Double> longitudeList;
    List<Double> latitudeList;
    List<String> disasterTypeList;

    boolean isPermissionGranted;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            // Permission Check
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            // Set default map camera starting location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(11.6737, 122.4816), 5.4f) );

            // Google Map UI Settings
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);

            // Get current location button
            googleMap.setMyLocationEnabled(true);

            // Check if GPS is turned on
            googleMap.setOnMyLocationButtonClickListener(() -> {
                if (!isGPSEnabled()) {
                    turnOnGPS();
                }
                return false;
            });

            // Add disaster markers
            addMarker(googleMap);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disasters, container, false);

        // Permission Check
        checkPermission();

        // Get reports list
        getReportsList();

        // Check if device has Google Play Services
        if (checkGooglePlayServices()) {
            // Initialize Map Fragment
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(callback);
            }

            // Check if user has granted permission
            if (isPermissionGranted) {
                // Enable GPS
                turnOnGPS();
            }
        } else {
            Toast.makeText(requireActivity(), "Google Play Services is required in order for maps to work!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        isPermissionGranted = true;
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
    }

    // Add disasters marker
    private void addMarker(GoogleMap googleMap) {
        int iconId = 0;

        // Check if list is empty
        if (!longitudeList.isEmpty() && !latitudeList.isEmpty()) {
            for (int i = 0; i < longitudeList.size(); i++) {
                // Set icon id for custom marker
                switch(disasterTypeList.get(i)) {
                    case "Typhoon":
                        iconId = R.drawable.ic_map_typhoon;
                        break;
                    case "Heavy Rain":
                        iconId = R.drawable.ic_map_heavy_rain;
                        break;
                    case "Landslide":
                        iconId = R.drawable.ic_map_landslide;
                        break;
                    case "Earthquake":
                        iconId = R.drawable.ic_map_earthquake;
                        break;
                    case "Fire":
                        iconId = R.drawable.ic_map_fire;
                        break;
                    case "Flood":
                        iconId = R.drawable.ic_map_flood;
                        break;
                    case "Tsunami":
                        iconId = R.drawable.ic_map_tsunami;
                        break;
                    case "Volcanic Eruption":
                        iconId = R.drawable.ic_map_volcanic_eruption;
                        break;
                }

                // Add marker to the map
                LatLng latLng = new LatLng(latitudeList.get(i), longitudeList.get(i));
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(bitmapDescriptor(requireActivity().getApplicationContext(), iconId))
                        .title(disasterTypeList.get(i)));
            }
        }
    }

    // Get reports list
    private void getReportsList() {
        longitudeList = new ArrayList<>();
        latitudeList = new ArrayList<>();
        disasterTypeList = new ArrayList<>();

        reportsReference = FirebaseDatabase.getInstance().getReference().child("Reports");
        reportsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                longitudeList.clear();
                latitudeList.clear();
                disasterTypeList.clear();

                // Add database data inside the list
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String longitude = String.valueOf(ds.child("longitude").getValue());
                    String latitude = String.valueOf(ds.child("latitude").getValue());
                    String disasterType = String.valueOf(ds.child("disasterType").getValue());

                    Double lon = Double.valueOf(longitude);
                    Double lat = Double.valueOf(latitude);

                    longitudeList.add(lon);
                    latitudeList.add(lat);
                    disasterTypeList.add(disasterType);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Check if GPS in turned on
    private boolean isGPSEnabled() {
        boolean isEnabled;
        LocationManager locationManager;
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return isEnabled;
    }

    // Turn on GPS
    private void turnOnGPS() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(requireActivity().getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(requireActivity(), 2);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    // Check if user has granted permission
    private void checkPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(requireActivity(), perms)) {
            isPermissionGranted = true;
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "This feature requires location permission in order to work!", 2, perms);
        }
    }

    // Check if device has Google Play Services
    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(requireActivity());
        return result == ConnectionResult.SUCCESS;
    }

    // Bitmap Descriptor for custom marker icons
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