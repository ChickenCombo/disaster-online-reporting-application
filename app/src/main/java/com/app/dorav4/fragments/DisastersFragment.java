package com.app.dorav4.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.app.dorav4.R;
import com.app.dorav4.models.Markers;
import com.app.dorav4.utils.MarkerClusterRenderer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class DisastersFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    ClusterManager<Markers> clusterManager;
    LocationRequest locationRequest;
    DatabaseReference reportsReference;
    boolean isPermissionGranted;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @SuppressLint("PotentialBehaviorOverride")
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            // Permission Check
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            // Set default map camera starting location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(11.6737, 122.4816), 5.4f));

            // Setup markers clustering
            clusterManager = new ClusterManager<>(requireActivity(), googleMap);
            clusterManager.setRenderer(new MarkerClusterRenderer(requireActivity(), googleMap, clusterManager));
            googleMap.setOnCameraIdleListener(clusterManager);
            googleMap.setOnMarkerClickListener(clusterManager);

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

            // Get disaster reports data
            getReports();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disasters, container, false);

        // Permission Check
        checkPermission();

        // Initialize Google Maps
        initializeMap();

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // Set to if user grants location permission
        isPermissionGranted = true;

        // Initialize Google Maps
        initializeMap();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
    }

    // Initialize map
    private void initializeMap() {
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
            MotionToast.Companion.darkToast(
                    requireActivity(),
                    "Error",
                    "Google Play services is required for this feature",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
            );
        }
    }

    // Get reports list
    private void getReports() {
        clusterManager.clearItems();

        reportsReference = FirebaseDatabase.getInstance().getReference().child("Reports");
        reportsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (snapshot.exists()) {
                        // Fetch disaster report data from the database
                        double longitude = Double.parseDouble(String.valueOf(ds.child("longitude").getValue()));
                        double latitude = Double.parseDouble(String.valueOf(ds.child("latitude").getValue()));
                        String disasterType = String.valueOf(ds.child("disasterType").getValue());
                        String address = String.valueOf(ds.child("address").getValue());

                        // Add data to map cluster manager
                        Markers offsetItem = new Markers(latitude, longitude, disasterType, "Address: " + address, 0);
                        clusterManager.addItem(offsetItem);
                    }
                }
                clusterManager.cluster();
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
}