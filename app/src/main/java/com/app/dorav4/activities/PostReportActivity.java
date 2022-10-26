package com.app.dorav4.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;
import com.app.dorav4.R;
import com.app.dorav4.utils.PushNotificationService;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class PostReportActivity extends AppCompatActivity {
    TextInputLayout tilDisasterType, tilDescription;
    AutoCompleteTextView tvDisasterType;
    TextInputEditText etDescription;
    ImageView ivBack, ivAddImage, ivGetLocation, ivReportingPolicy;
    Button btnReport;
    Uri photoUri;
    AlertDialog dialog;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss");

    DatabaseReference usersReference, reportsReference, tokensReference;
    StorageReference reportStorageReference;

    double longitude, latitude;
    String address = "";

    JSONArray tokens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_report);

        // Change status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(PostReportActivity.this, R.color.background));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        tokens = new JSONArray();
        geocoder = new Geocoder(PostReportActivity.this, Locale.getDefault());

        tilDisasterType = findViewById(R.id.tilDisasterType);
        tilDescription = findViewById(R.id.tilDescription);
        tvDisasterType = findViewById(R.id.tvDisasterType);
        etDescription = findViewById(R.id.etDescription);
        ivBack = findViewById(R.id.ivBack);
        ivAddImage = findViewById(R.id.ivAddImage);
        ivGetLocation = findViewById(R.id.ivGetLocation);
        ivReportingPolicy = findViewById(R.id.ivReportingPolicy);
        btnReport = findViewById(R.id.btnReport);

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        reportsReference = FirebaseDatabase.getInstance().getReference().child("Reports");
        tokensReference = FirebaseDatabase.getInstance().getReference().child("Tokens");
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

        // ivReportingPolicy OnClickListener
        ivReportingPolicy.setOnClickListener(v -> showReportingPolicy());

        // btnReport OnClickListener
        btnReport.setOnClickListener(v -> postReport());

        // Get notification tokens
        getTokens();
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
                MotionToast.Companion.darkToast(
                        this,
                        "Error",
                        "DORA v4 requires location permission",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                );
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
            MotionToast.Companion.darkToast(
                    this,
                    "Error",
                    "Invalid location, please try again",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
            );
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
            MotionToast.Companion.darkToast(
                    this,
                    "Error",
                    "Please upload an image of the incident",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
            );
        }

        // Add to Firebase
        if (isDisasterTypeEmpty && isDescriptionEmpty && isImageEmpty && isAddressEmpty) {
            // Progress Dialog
            MaterialDialog pDialog = new MaterialDialog.Builder(this)
                    .setTitle("Loading")
                    .setMessage("Posting your disaster report, please wait")
                    .setAnimation(R.raw.lottie_loading)
                    .setCancelable(false)
                    .build();

            LottieAnimationView animationView = pDialog.getAnimationView();
            animationView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            animationView.setRenderMode(RenderMode.SOFTWARE);
            animationView.setPadding(0, 64, 0, 0);

            pDialog.show();

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
                               pDialog.dismiss();

                               MotionToast.Companion.darkToast(
                                       this,
                                       "Success",
                                       "Disaster report has been submitted",
                                       MotionToastStyle.SUCCESS,
                                       MotionToast.GRAVITY_BOTTOM,
                                       MotionToast.LONG_DURATION,
                                       ResourcesCompat.getFont(this, R.font.helvetica_regular)
                               );

                               // Send push notification
                               PushNotificationService.multicastNotification(PostReportActivity.this, tokens, disasterType, address);

                               finish();
                           } else {
                               pDialog.dismiss();
                               MotionToast.Companion.darkToast(
                                       this,
                                       "Error",
                                       "Disaster report submission failed, please try again",
                                       MotionToastStyle.ERROR,
                                       MotionToast.GRAVITY_BOTTOM,
                                       MotionToast.LONG_DURATION,
                                       ResourcesCompat.getFont(this, R.font.helvetica_regular)
                               );
                           }
                       });
                   });
               } else {
                   pDialog.dismiss();
                   MotionToast.Companion.darkToast(
                           this,
                           "Error",
                           "Disaster report submission failed, please try again",
                           MotionToastStyle.ERROR,
                           MotionToast.GRAVITY_BOTTOM,
                           MotionToast.LONG_DURATION,
                           ResourcesCompat.getFont(this, R.font.helvetica_regular)
                   );
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
        if (!(ActivityCompat.checkSelfPermission(PostReportActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            // Request permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Turn on GPS
        if (!isGPSEnabled()) {
            turnOnGPS();
        }

        // Get current location
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                // Get address
                Geocoder geocoder = new Geocoder(PostReportActivity.this, Locale.getDefault());
                try {
                    while (address.equals("") || address.isEmpty()) {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        address = addressList.get(0).getAddressLine(0);
                    }

                    // Show toast message
                    MotionToast.Companion.darkToast(
                            this,
                            "INFO",
                            "Fetching your location",
                            MotionToastStyle.INFO,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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

                    public void invoke(@NotNull Intent it) {
                        Intrinsics.checkNotNullParameter(it, "it");
                        launcher.launch(it);
                    }
                });
    }

    // Get tokens for push notification
    private void getTokens() {
        tokensReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String token = String.valueOf(ds.child("token").getValue());
                    tokens.put(token);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Show reporting policy dialog
    private void showReportingPolicy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_reporting_policy, null);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        // btnSubmit OnClickListener
        btnSubmit.setOnClickListener(v -> dialog.dismiss());

        builder.setCancelable(false);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }
}