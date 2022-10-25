package com.app.dorav4.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.app.dorav4.R;
import com.google.android.material.card.MaterialCardView;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
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

        // Change status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(HotlinesActivity.this, R.color.background));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());

        // cv911 OnClickListener
        cv911.setOnClickListener(v -> callNumber("911", "National Emergency"));

        // cv1555 OnClickListener
        cv1555.setOnClickListener(v -> callNumber("1555", "Department of Health"));

        // cv143 OnClickListener
        cv143.setOnClickListener(v -> callNumber("143", "Philippine Red Cross"));

        // cv163 OnClickListener
        cv163.setOnClickListener(v -> callNumber("163", "Bantay Bata"));

        // cv8888 OnClickListener
        cv8888.setOnClickListener(v -> callNumber("8888", "National Complaint"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // Call emergency hotline
    private void callNumber(String number, String agency) {
        intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));

        String[] perms = {Manifest.permission.CALL_PHONE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Show confirmation dialog
            MaterialDialog mDialog = new MaterialDialog.Builder(this)
                    .setTitle("Call " + agency + "?")
                    .setMessage("Are you sure want to call " + number + "?")
                    .setAnimation(R.raw.lottie_call)
                    .setCancelable(false)
                    .setPositiveButton("Call", R.drawable.ic_call, (dialogInterface, which) -> {
                        dialogInterface.dismiss();
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", R.drawable.ic_cancel, (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();

            LottieAnimationView animationView = mDialog.getAnimationView();
            animationView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            animationView.setPadding(0, 64, 0, 0);

            mDialog.show();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "This feature requires call permission in order to work!", 2, perms);
        }
    }
}