package com.app.dorav4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.app.dorav4.R;

public class GuidesListActivity extends AppCompatActivity {
    ImageView ivBack;
    CardView cvEarthquake, cvFire, cvTyphoon, cvVolcanicEruption, cvTsunami, cvLandslide, cvFlood;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guides_list);

        ivBack = findViewById(R.id.ivBack);
        cvEarthquake = findViewById(R.id.cvEarthquake);
        cvFire = findViewById(R.id.cvFire);
        cvTyphoon = findViewById(R.id.cvTyphoon);
        cvVolcanicEruption = findViewById(R.id.cvVolcanicEruption);
        cvTsunami = findViewById(R.id.cvTsunami);
        cvLandslide = findViewById(R.id.cvLandslide);
        cvFlood = findViewById(R.id.cvFlood);

        // Change status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(GuidesListActivity.this, R.color.background));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());

        // cvEarthquake OnClickListener
        cvEarthquake.setOnClickListener(v -> {
            intent = new Intent(GuidesListActivity.this, GuidesActivity.class);
            intent.putExtra("fileName", "earthquake.html");
            intent.putExtra("title", "Earthquake");
            startActivity(intent);
        });

        // cvFire OnClickListener
        cvFire.setOnClickListener(v -> {
            intent = new Intent(GuidesListActivity.this, GuidesActivity.class);
            intent.putExtra("fileName", "fire.html");
            intent.putExtra("title", "Fire");
            startActivity(intent);
        });

        // cvTyphoon OnClickListener
        cvTyphoon.setOnClickListener(v -> {
            intent = new Intent(GuidesListActivity.this, GuidesActivity.class);
            intent.putExtra("fileName", "tropical_cyclone.html");
            intent.putExtra("title", "Tropical Cyclone");
            startActivity(intent);
        });

        // cvVolcanicEruption OnClickListener
        cvVolcanicEruption.setOnClickListener(v -> {
            intent = new Intent(GuidesListActivity.this, GuidesActivity.class);
            intent.putExtra("fileName", "volcanic_eruption.html");
            intent.putExtra("title", "Volcanic Eruption");
            startActivity(intent);
        });

        // cvTsunami OnClickListener
        cvTsunami.setOnClickListener(v -> {
            intent = new Intent(GuidesListActivity.this, GuidesActivity.class);
            intent.putExtra("fileName", "tsunami.html");
            intent.putExtra("title", "Tsunami");
            startActivity(intent);
        });

        // cvLandslide OnClickListener
        cvLandslide.setOnClickListener(v -> {
            intent = new Intent(GuidesListActivity.this, GuidesActivity.class);
            intent.putExtra("fileName", "landslide.html");
            intent.putExtra("title", "Landslide");
            startActivity(intent);
        });

        // cvFlood OnClickListener
        cvFlood.setOnClickListener(v -> {
            intent = new Intent(GuidesListActivity.this, GuidesActivity.class);
            intent.putExtra("fileName", "flood.html");
            intent.putExtra("title", "Flood");
            startActivity(intent);
        });
    }
}