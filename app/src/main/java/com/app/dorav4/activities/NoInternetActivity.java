package com.app.dorav4.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.app.dorav4.R;

public class NoInternetActivity extends AppCompatActivity {
    Button btnTryAgain, btnOfflineMode;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        btnTryAgain = findViewById(R.id.btnTryAgain);
        btnOfflineMode = findViewById(R.id.btnOfflineMode);

        // btnTryAgain OnClickListener
        btnTryAgain.setOnClickListener(v -> {
            if (!isConnected(NoInternetActivity.this)) {
                Toast.makeText(NoInternetActivity.this, "Connection failed, please try again!", Toast.LENGTH_SHORT).show();
            } else {
                intent = new Intent(NoInternetActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // btnOfflineMode OnClickListener
        btnOfflineMode.setOnClickListener(v -> {
            intent = new Intent(NoInternetActivity.this, OfflineDashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Check internet connection
    public boolean isConnected(Context context) {
        boolean status = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
            status = capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }
}