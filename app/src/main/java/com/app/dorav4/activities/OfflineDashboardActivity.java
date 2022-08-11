package com.app.dorav4.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.app.dorav4.R;
import com.google.android.material.card.MaterialCardView;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class OfflineDashboardActivity extends AppCompatActivity {
    MaterialCardView cvGuides, cvHotlines, cvBluetoothChat, cvOnlineFeatures;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_dashboard);

        cvGuides = findViewById(R.id.cvGuides);
        cvHotlines = findViewById(R.id.cvHotlines);
        cvBluetoothChat = findViewById(R.id.cvBluetoothChat);
        cvOnlineFeatures = findViewById(R.id.cvOnlineFeatures);

        // Disaster Preparedness Guides OnClickListener
        cvGuides.setOnClickListener(v ->{
            Toast.makeText(OfflineDashboardActivity.this, "Guides Clicked", Toast.LENGTH_SHORT).show();
            // intent = new Intent(OfflineDashboardActivity.this, GuidesActivity.class);
            // startActivity(intent);
        });

        // Emergency Hotlines OnClickListener
        cvHotlines.setOnClickListener(v ->{
            Toast.makeText(OfflineDashboardActivity.this, "Hotlines Clicked", Toast.LENGTH_SHORT).show();
            // intent = new Intent(OfflineDashboardActivity.this, HotlinesActivity.class);
            // startActivity(intent);
        });

        // Bluetooth Chat OnClickListener
        cvBluetoothChat.setOnClickListener(v ->{
            Toast.makeText(OfflineDashboardActivity.this, "Offline Chat Clicked", Toast.LENGTH_SHORT).show();
            // intent = new Intent(OfflineDashboardActivity.this, HotlinesActivity.class);
            // startActivity(intent);
        });

        // Access Online Features OnClickListener
        cvOnlineFeatures.setOnClickListener(v ->{
            intent = new Intent(OfflineDashboardActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        });
    }
}