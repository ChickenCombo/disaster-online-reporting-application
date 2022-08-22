package com.app.dorav4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dorav4.R;
import com.google.android.material.card.MaterialCardView;

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
            intent = new Intent(OfflineDashboardActivity.this, GuidesActivity.class);
            startActivity(intent);
        });

        // Emergency Hotlines OnClickListener
        cvHotlines.setOnClickListener(v ->{
            intent = new Intent(OfflineDashboardActivity.this, HotlinesActivity.class);
            startActivity(intent);
        });

        // Bluetooth Chat OnClickListener
        cvBluetoothChat.setOnClickListener(v ->{
            Toast.makeText(OfflineDashboardActivity.this, "Offline Chat Clicked", Toast.LENGTH_SHORT).show();
            // intent = new Intent(OfflineDashboardActivity.this, BluetoothChat.class);
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