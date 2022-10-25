package com.app.dorav4.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.app.dorav4.R;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class NoInternetActivity extends AppCompatActivity {
    Button btnTryAgain, btnOfflineMode;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        // Change status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(NoInternetActivity.this, R.color.background));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        btnTryAgain = findViewById(R.id.btnTryAgain);
        btnOfflineMode = findViewById(R.id.btnOfflineMode);

        // btnTryAgain OnClickListener
        btnTryAgain.setOnClickListener(v -> {
            if (!isConnected(NoInternetActivity.this)) {
                MotionToast.Companion.darkToast(
                        this,
                        "No Internet",
                        "Connection failed, please try again",
                        MotionToastStyle.NO_INTERNET,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                );
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