package com.app.dorav4.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.app.dorav4.R;
import com.google.android.material.card.MaterialCardView;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import pub.devrel.easypermissions.EasyPermissions;

public class HotlinesActivity extends AppCompatActivity {
    MaterialCardView cv911, cv1555, cv143, cv163, cv8888;
    ImageView ivBack;
    Intent intent;
    Dialog dialog;

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
            // Show confirmation dialog
            MaterialDialog mDialog = new MaterialDialog.Builder(this)
                    .setTitle("Call?")
                    .setMessage("Are you sure want to call " + number + "?")
                    .setCancelable(false)
                    .setPositiveButton("Call", R.drawable.ic_call, (dialogInterface, which) -> {
                        dialogInterface.dismiss();
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", R.drawable.ic_cancel, (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();

            mDialog.show();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "This feature requires call permission in order to work!", 2, perms);
        }
    }
}