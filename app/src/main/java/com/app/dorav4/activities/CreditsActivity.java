package com.app.dorav4.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dorav4.R;

public class CreditsActivity extends AppCompatActivity {
    TextView tvToolbarHeader;
    ImageView ivBack;
    WebView wvCredits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        tvToolbarHeader = findViewById(R.id.tvToolbarHeader);
        ivBack = findViewById(R.id.ivBack);
        wvCredits = findViewById(R.id.wvCredits);

        tvToolbarHeader.setText(R.string.credits_header);

        String fileName = "credits.html";
        wvCredits.loadUrl("file:///android_asset/" + fileName);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }
}