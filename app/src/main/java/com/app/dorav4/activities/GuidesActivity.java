package com.app.dorav4.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.dorav4.R;

public class GuidesActivity extends AppCompatActivity {
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guides);

        ivBack = findViewById(R.id.ivBack);

        // Change status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(GuidesActivity.this, R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }
}