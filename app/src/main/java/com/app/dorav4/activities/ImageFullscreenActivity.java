package com.app.dorav4.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.app.dorav4.R;
import com.squareup.picasso.Picasso;

public class ImageFullscreenActivity extends AppCompatActivity {
    ImageView ivReportPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        ivReportPicture = findViewById(R.id.ivReportPicture);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set image
        String reportPicture = getIntent().getStringExtra("reportPicture");
        Picasso.get().load(reportPicture).into(ivReportPicture);
    }
}