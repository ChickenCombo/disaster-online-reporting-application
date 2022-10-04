package com.app.dorav4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.app.dorav4.R;

public class GuidesActivity extends AppCompatActivity {
    ImageView ivBack;
    CardView cvEarthquake, cvFire, cvTyphoon, cvVolcanicEruption, cvTsunami, cvLandslide;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guides);

        ivBack = findViewById(R.id.ivBack);
        cvEarthquake = findViewById(R.id.cvEarthquake);
        cvFire = findViewById(R.id.cvFire);
        cvTyphoon = findViewById(R.id.cvTyphoon);
        cvVolcanicEruption = findViewById(R.id.cvVolcanicEruption);
        cvTsunami = findViewById(R.id.cvTsunami);
        cvLandslide = findViewById(R.id.cvLandslide);

        // Change status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(GuidesActivity.this, R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());

        // cvEarthquake OnClickListener
        cvEarthquake.setOnClickListener(v -> {
            intent = new Intent(GuidesActivity.this, EarthquakeActivity.class);
            startActivity(intent);
        });

        // cvFire OnClickListener
        cvFire.setOnClickListener(v -> {
            intent = new Intent(GuidesActivity.this, FireActivity.class);
            startActivity(intent);
        });

        // cvTyphoon OnClickListener
        cvTyphoon.setOnClickListener(v -> {
            intent = new Intent(GuidesActivity.this, TyphoonActivity.class);
            startActivity(intent);
        });

        // cvVolcanicEruption OnClickListener
        cvVolcanicEruption.setOnClickListener(v -> {
            intent = new Intent(GuidesActivity.this, VolcanicEruptionActivity.class);
            startActivity(intent);
        });

        // cvTsunami OnClickListener
        cvTsunami.setOnClickListener(v -> {
            intent = new Intent(GuidesActivity.this, TsunamiActivity.class);
            startActivity(intent);
        });

        // cvLandslide OnClickListener
        cvLandslide.setOnClickListener(v -> {
            intent = new Intent(GuidesActivity.this, LandslideActivity.class);
            startActivity(intent);
        });
    }
}