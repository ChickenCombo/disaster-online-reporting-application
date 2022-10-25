package com.app.dorav4.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.dorav4.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class OfflineDashboardActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    MaterialCardView cvGuides, cvHotlines, cvBluetoothChat, cvOnlineFeatures;
    Intent intent;

    public String[] REQUIRED_PERMISSIONS;
    private final int REQUEST_CODE_REQUIRED_PERMISSIONS = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_dashboard);

        // Change status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(OfflineDashboardActivity.this, R.color.background));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        cvGuides = findViewById(R.id.cvGuides);
        cvHotlines = findViewById(R.id.cvHotlines);
        cvBluetoothChat = findViewById(R.id.cvBluetoothChat);
        cvOnlineFeatures = findViewById(R.id.cvOnlineFeatures);

        // Set permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            REQUIRED_PERMISSIONS = new String[] {
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT
            };
        } else {
            REQUIRED_PERMISSIONS = new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
        }

        // Disaster Preparedness Guides OnClickListener
        cvGuides.setOnClickListener(v -> {
            intent = new Intent(OfflineDashboardActivity.this, GuidesListActivity.class);
            startActivity(intent);
        });

        // Emergency Hotlines OnClickListener
        cvHotlines.setOnClickListener(v -> {
            intent = new Intent(OfflineDashboardActivity.this, HotlinesActivity.class);
            startActivity(intent);
        });

        // Bluetooth Chat OnClickListener
        cvBluetoothChat.setOnClickListener(v -> {
            if (EasyPermissions.hasPermissions(this, REQUIRED_PERMISSIONS)) {
                intent = new Intent(OfflineDashboardActivity.this, BluetoothChatActivity.class);
                startActivity(intent);
            } else {
                EasyPermissions.requestPermissions(this, "This feature requires bluetooth permission in order to work!", REQUEST_CODE_REQUIRED_PERMISSIONS, REQUIRED_PERMISSIONS);
            }
        });

        // Access Online Features OnClickListener
        cvOnlineFeatures.setOnClickListener(v -> {
            intent = new Intent(OfflineDashboardActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        intent = new Intent(OfflineDashboardActivity.this, BluetoothChatActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms)   {
        finish();
    }
}