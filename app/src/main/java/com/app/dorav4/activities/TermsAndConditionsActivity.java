package com.app.dorav4.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dorav4.R;

public class TermsAndConditionsActivity extends AppCompatActivity {
    TextView tvToolbarHeader;
    ImageView ivBack;
    WebView wvTermsAndConditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        tvToolbarHeader = findViewById(R.id.tvToolbarHeader);
        ivBack = findViewById(R.id.ivBack);
        wvTermsAndConditions = findViewById(R.id.wvTermsAndConditions);

        tvToolbarHeader.setText(R.string.terms_and_conditions_header);

        String fileName = "terms_and_conditions.html";
        wvTermsAndConditions.loadUrl("file:///android_asset/" + fileName);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }
}