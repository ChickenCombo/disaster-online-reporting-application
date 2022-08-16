package com.app.dorav4.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.dorav4.R;
import com.google.android.material.card.MaterialCardView;

import pub.devrel.easypermissions.EasyPermissions;

public class HotlinesActivity extends AppCompatActivity {
    MaterialCardView cv911, cv1555, cv143, cv163, cv8888;
    ImageView ivBack;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotlines);

        cv911 = findViewById(R.id.cv911);
        cv1555 = findViewById(R.id.cv1555);
        cv143 = findViewById(R.id.cv143);
        cv163 = findViewById(R.id.cv163);
        cv8888 = findViewById(R.id.cv8888);
        ivBack = findViewById(R.id.ivBack);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());

        // cv911 OnClickListener
        cv911.setOnClickListener(v -> callNumber("911"));

        // cv1555 OnClickListener
        cv1555.setOnClickListener(v -> callNumber("1555"));

        // cv143 OnClickListener
        cv143.setOnClickListener(v -> callNumber("143"));

        // cv163 OnClickListener
        cv163.setOnClickListener(v -> callNumber("163"));

        // cv8888 OnClickListener
        cv8888.setOnClickListener(v -> callNumber("8888"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // Call emergency hotline
    private void callNumber(String number) {
        intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));

        String[] perms = {Manifest.permission.CALL_PHONE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(HotlinesActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Emergency Call");
            builder.setMessage("Are you sure you want to call " + number + "?");
            builder.setPositiveButton("Call", (dialog, which) -> startActivity(intent));
            builder.setNegativeButton("Cancel", (dialog, which) -> { });
            builder.create().show();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "This feature requires call permission in order to work!", 2, perms);
        }
    }
}