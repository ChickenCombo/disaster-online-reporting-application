package com.app.dorav4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dorav4.R;

public class GuidesActivity extends AppCompatActivity {
    TextView tvToolbarHeader;
    ImageView ivBack;
    WebView wvGuide;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guides);

        tvToolbarHeader = findViewById(R.id.tvToolbarHeader);
        ivBack = findViewById(R.id.ivBack);
        wvGuide = findViewById(R.id.wvGuide);

        // Get data from previous intent
        intent = getIntent();
        String fileName = intent.getStringExtra("fileName");
        String title = intent.getStringExtra("title");

        // Set data
        tvToolbarHeader.setText(title);
        wvGuide.loadUrl("file:///android_asset/" + fileName);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }
}