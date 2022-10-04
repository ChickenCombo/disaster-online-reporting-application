package com.app.dorav4.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dorav4.R;

public class TsunamiActivity extends AppCompatActivity {
    TextView tvToolbarHeader;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tsunami);

        tvToolbarHeader = findViewById(R.id.tvToolbarHeader);
        ivBack = findViewById(R.id.ivBack);

        tvToolbarHeader.setText(R.string.tsunami);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }
}