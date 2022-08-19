package com.app.dorav4.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.app.dorav4.R;

public class GuidesActivity extends AppCompatActivity {
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guides);

        ivBack = findViewById(R.id.ivBack);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }
}