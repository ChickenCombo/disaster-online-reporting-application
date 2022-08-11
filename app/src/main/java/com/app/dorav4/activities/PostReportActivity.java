package com.app.dorav4.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.app.dorav4.R;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class PostReportActivity extends AppCompatActivity {
    TextInputLayout tilDisasterType, tilDescription;
    AutoCompleteTextView tvDisasterType;
    TextInputEditText etDescription;
    ImageView ivBack, ivAddImage, ivGetLocation;
    Button btnReport;
    Uri photoUri;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss");

    ProgressDialog progressDialog;

    DatabaseReference usersReference, reportsReference;
    StorageReference reportStorageReference;

    double longitude, latitude;
    String address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_report);

        geocoder = new Geocoder(PostReportActivity.this, Locale.getDefault());

        tilDisasterType = findViewById(R.id.tilDisasterType);
        tilDescription = findViewById(R.id.tilDescription);
        tvDisasterType = findViewById(R.id.tvDisasterType);
        etDescription = findViewById(R.id.etDescription);
        ivBack = findViewById(R.id.ivBack);
        ivAddImage = findViewById(R.id.ivAddImage);
        ivGetLocation = findViewById(R.id.ivGetLocation);
        btnReport = findViewById(R.id.btnReport);

        progressDialog = new ProgressDialog(PostReportActivity.this);

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        reportsReference = FirebaseDatabase.getInstance().getReference().child("Reports");
        reportStorageReference = FirebaseStorage.getInstance().getReference().child("ReportPictures");

        // Set disasters dropdown menu items
        String[] disasters = getResources().getStringArray(R.array.disasters);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(PostReportActivity.this, R.layout.dropdown_disasters, disasters);
        tvDisasterType.setAdapter(arrayAdapter);

        // Location
        locationRequest = LocationRequest.create();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(PostReportActivity.this);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());

        // ivAddImage OnClickListener
        ivAddImage.setOnClickListener(v -> chooseImage());

        // ivGetLocation OnClickListener
        ivGetLocation.setOnClickListener(v -> getLocation());

        // btnReport OnClickListener
        btnReport.setOnClickListener(v -> postReport());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        // Set disasters dropdown menu items on resume
        String[] disasters = getResources().getStringArray(R.array.disasters);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(PostReportActivity.this, R.layout.dropdown_disasters, disasters);
        tvDisasterType.setAdapter(arrayAdapter);
    }

    // Get location if user grants permission, else, close the application
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(PostReportActivity.this, "Application requires location permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // Post report to Firebase
    private void postReport() {
        boolean isDisasterTypeEmpty = false, isDescriptionEmpty = false, isImageEmpty = false, isAddressEmpty = false;
        String disasterType = Objects.requireNonNull(tvDisasterType.getText().toString());
        String description = Objects.requireNonNull(etDescription.getText()).toString();

        // Validate address
        if (address.isEmpty()) {
            Toast.makeText(PostReportActivity.this, "Invalid location, please try again", Toast.LENGTH_SHORT).show();
        } else {
            isAddressEmpty = true;
        }

        // Validate disaster type
        if (disasterType.equals("Disaster Type")) {
            tilDisasterType.setError("Please select the type of disaster");
        } else {
            isDisasterTypeEmpty = true;
            tilDisasterType.setError(null);
        }

        // Validate description
        if (description.isEmpty()) {
            tilDescription.setError("Please select the type of disaster");
        } else {
            isDescriptionEmpty = true;
            tilDescription.setError(null);
        }

        // Validate image
        if (photoUri != null && !photoUri.equals(Uri.EMPTY)) {
            isImageEmpty = true;
        } else {
            Toast.makeText(PostReportActivity.this, "Please upload an image of the incident!", Toast.LENGTH_SHORT).show();
        }

        // Add to Firebase
        if (isDisasterTypeEmpty && isDescriptionEmpty && isImageEmpty && isAddressEmpty) {
            // ProgressDialog
            progressDialog.setTitle("Report");
            progressDialog.setMessage("Posting your disaster report");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Get date and time of post
            Date date = new Date();
            String formattedDate = format.format(date);
            long epochDate = date.getTime();

            // Save data to Firebase
            reportStorageReference.child(String.valueOf(epochDate)).putFile(photoUri).addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   reportStorageReference.child(String.valueOf(epochDate)).getDownloadUrl().addOnSuccessListener(uri -> {
                       HashMap<String, Object> hashMap = new HashMap<>();
                       hashMap.put("date", formattedDate);
                       hashMap.put("reportPicture", uri.toString());
                       hashMap.put("description", description);
                       hashMap.put("disasterType", disasterType);
                       hashMap.put("fullName", MainActivity.fullName);
                       hashMap.put("profilePicture", MainActivity.profilePicture);
                       hashMap.put("userId", MainActivity.userId);
                       hashMap.put("reportId", String.valueOf(epochDate));
                       hashMap.put("longitude", String.valueOf(longitude));
                       hashMap.put("latitude", String.valueOf(latitude));
                       hashMap.put("address", address);
                       hashMap.put("comments", "0");
                       hashMap.put("upvotes", "0");

                       reportsReference.child(String.valueOf(epochDate)).updateChildren(hashMap).addOnCompleteListener(o -> {
                           if (task.isSuccessful()) {
                               progressDialog.dismiss();

                               Toast.makeText(PostReportActivity.this, "Report has been submitted!", Toast.LENGTH_SHORT).show();
                               finish();
                           } else {
                               progressDialog.dismiss();
                               Toast.makeText(PostReportActivity.this, "Post failed: " + Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                           }
                       });
                   });
               } else {
                   progressDialog.dismiss();
                   Toast.makeText(PostReportActivity.this, "Post failed: " + Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
               }
            });
        }
    }

    // Select image from gallery
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            photoUri = result.getData().getData();
        } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
            ImagePicker.Companion.getError(result.getData());
        }
    });

    // Get the user's current location
    private void getLocation() {
        // User has permission
        if (ActivityCompat.checkSelfPermission(PostReportActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Turn on GPS
            if (!isGPSEnabled()) {
                turnOnGPS();
            }

            Toast.makeText(PostReportActivity.this, "Fetching your current location", Toast.LENGTH_SHORT).show();

            // Get current location
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    // Get address
                    Geocoder geocoder = new Geocoder(PostReportActivity.this, Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        address = addressList.get(0).getAddressLine(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            // Request permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // Check if GPS in turned on
    private boolean isGPSEnabled() {
        boolean isEnabled;
        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return isEnabled;
    }

    // Turn on GPS
    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(PostReportActivity.this, 2);
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

    // Choose image from gallery or camera
    private void chooseImage() {
        ImagePicker.Companion.with(this)
                .crop()
                .provider(ImageProvider.BOTH)
                .createIntentFromDialog(new Function1(){
                    public Object invoke(Object var1) {
                        this.invoke((Intent) var1);
                        return Unit.INSTANCE;
                    }

                    public final void invoke(@NotNull Intent it) {
                        Intrinsics.checkNotNullParameter(it, "it");
                        launcher.launch(it);
                    }
                });
    }
}