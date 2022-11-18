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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;
import com.app.dorav4.R;
import com.app.dorav4.adapters.InfoWindowAdapter;
import com.app.dorav4.models.Markers;
import com.app.dorav4.utils.MarkerClusterRenderer;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import pub.devrel.easypermissions.EasyPermissions;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class EvacuateFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    ClusterManager<Markers> clusterManager;
    LocationRequest locationRequest;
    Markers nearestMarker;
    SupportMapFragment mapFragment;
    ImageButton btnNearest;
    FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference evacuationCentersReference;
    boolean isPermissionGranted;
    double minimumDistance;
    int index;

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

            btnNearest.setOnClickListener(v -> {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nearestMarker.getPosition(), 20));
            });

            // Setup markers clustering
            clusterManager = new ClusterManager<>(requireActivity(), googleMap);
            clusterManager.setRenderer(new MarkerClusterRenderer(requireActivity(), googleMap, clusterManager));
            clusterManager.getMarkerCollection().setInfoWindowAdapter(new InfoWindowAdapter(requireActivity()));
            googleMap.setOnCameraIdleListener(clusterManager);
            googleMap.setOnMarkerClickListener(clusterManager);

            // Google Map UI Settings
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);

            // Get current location button
            googleMap.setMyLocationEnabled(true);

            // Check if GPS is turned on
            googleMap.setOnMyLocationButtonClickListener(() -> {
                if (!isGPSEnabled()) {
                    turnOnGPS();
                }

                setNearbyEvacuationButton();
                return false;
            });

            // Get evacuation centers data
            getEvacuationCenters();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evacuate, container, false);

        btnNearest = view.findViewById(R.id.btnNearest);

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

    // Set nearby evacuation center button's position and visibility
    private void setNearbyEvacuationButton() {
        try {
            final ViewGroup parent = (ViewGroup) mapFragment.requireView().findViewWithTag("GoogleMapMyLocationButton").getParent();
            parent.post(() -> {
                try {
                    // Get the "My Location" button's view
                    View defaultButton = mapFragment.requireView().findViewWithTag("GoogleMapMyLocationButton");

                    // Get the "Nearby Evacuation" button's view
                    ViewGroup customButtonParent = (ViewGroup) btnNearest.getParent();
                    customButtonParent.removeView(btnNearest);

                    // Add custom button view to Google Maps control button parent
                    ViewGroup defaultButtonParent = (ViewGroup) defaultButton.getParent();
                    defaultButtonParent.addView(btnNearest);

                    // Create layout with same size as default Google Maps control button (38x38)
                    float size =  40 * requireActivity().getResources().getDisplayMetrics().density;
                    RelativeLayout.LayoutParams customButtonLayoutParams = new RelativeLayout.LayoutParams((int) size, (int) size);

                    // Align the custom button below the location button
                    customButtonLayoutParams.addRule(RelativeLayout.ALIGN_LEFT, defaultButton.getId());
                    customButtonLayoutParams.addRule(RelativeLayout.BELOW, defaultButton.getId());
                    customButtonLayoutParams.topMargin = 8;
                    customButtonLayoutParams.rightMargin = 8;

                    // Add padding
                    btnNearest.setAlpha(defaultButton.getAlpha());
                    btnNearest.setPadding(defaultButton.getPaddingLeft(), defaultButton.getPaddingTop(), defaultButton.getPaddingRight(), defaultButton.getPaddingBottom());

                    // Apply settings and set visibility
                    btnNearest.setLayoutParams(customButtonLayoutParams);
                    btnNearest.setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Initialize map
    private void initializeMap() {
        // Check if device has Google Play Services
        if (checkGooglePlayServices()) {
            // Initialize Map Fragment
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
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

    // Get evacuation areas list
    private void getEvacuationCenters() {
        // Progress Dialog
        MaterialDialog pDialog = new MaterialDialog.Builder(requireActivity())
                .setTitle("Loading")
                .setMessage("Fetching evacuation areas, please wait")
                .setAnimation(R.raw.lottie_loading)
                .setCancelable(false)
                .build();

        LottieAnimationView animationView = pDialog.getAnimationView();
        animationView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        animationView.setRenderMode(RenderMode.SOFTWARE);
        animationView.setPadding(0, 64, 0, 0);

        pDialog.show();

        clusterManager.clearItems();

        // Get user's current location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                double currentLongitude = location.getLongitude();
                double currentLatitude = location.getLatitude();

                evacuationCentersReference = FirebaseDatabase.getInstance().getReference("EvacuationCenters");
                evacuationCentersReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (snapshot.exists()) {
                                // Fetch evacuation center data from the database
                                double longitude = Objects.requireNonNull((Double) ds.child("longitude").getValue());
                                double latitude =  Objects.requireNonNull((Double) ds.child("latitude").getValue());
                                String evacuationCenterName = (String) ds.child("evacuationCenterName").getValue();
                                String address = (String) ds.child("location").getValue();

                                // Calculate distance between current location and evacuation center's location
                                double distance = computeDistance(currentLatitude, currentLongitude, latitude, longitude);

                                // Add data to map cluster manager
                                Markers offsetItem = new Markers(latitude, longitude, evacuationCenterName, "Address: " + address, distance);

                                // Find the nearest evacuation center
                                if (index == 0) {
                                    minimumDistance = distance;
                                } else if (minimumDistance > distance) {
                                    minimumDistance = distance;
                                    nearestMarker = offsetItem;
                                }
                                index++;

                                clusterManager.addItem(offsetItem);
                            }
                        }
                        clusterManager.cluster();
                        pDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        pDialog.dismiss();
                    }
                });
            }
        });
    }

    // Compute distance between coordinates
    private double computeDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    // Degree to radian
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // Radian to degree
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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