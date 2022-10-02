package com.app.dorav4.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dorav4.R;

public class PrivacyPolicyActivity extends AppCompatActivity {
    TextView tvToolbarHeader;
    ImageView ivBack;
    WebView wvPrivacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        tvToolbarHeader = findViewById(R.id.tvToolbarHeader);
        ivBack = findViewById(R.id.ivBack);
        wvPrivacyPolicy = findViewById(R.id.wvPrivacyPolicy);

        tvToolbarHeader.setText(R.string.privacy_policy_header);

        String fileName = "privacy_policy.html";
        wvPrivacyPolicy.loadUrl("file:///android_asset/" + fileName);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }
}