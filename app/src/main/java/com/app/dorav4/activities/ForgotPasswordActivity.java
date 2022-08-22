package com.app.dorav4.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.app.dorav4.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextInputLayout tilEmailAddress;
    TextInputEditText etEmailAddress;
    ImageView ivBack;
    Button btnForgotPassword;

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        tilEmailAddress = findViewById(R.id.tilEmailAddress);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        ivBack = findViewById(R.id.ivBack);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);

        mAuth = FirebaseAuth.getInstance();

        // btnForgotPassword OnClickListener
        btnForgotPassword.setOnClickListener(v -> resetPassword());

        // ivBack OnClickListener
        ivBack.setOnClickListener(v -> finish());
    }

    private void resetPassword() {
        boolean isEmailValid = false;
        String email = Objects.requireNonNull(etEmailAddress.getText()).toString().trim();

        // Validate email address
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmailAddress.setError("Invalid email address, please try again.");
        } else {
            isEmailValid = true;
            tilEmailAddress.setError(null);
        }

        if (isEmailValid) {
            // ProgressDialog
            progressDialog.setTitle("Password Reset");
            progressDialog.setMessage("Resetting your password");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    MotionToast.Companion.darkToast(
                            this,
                            "Info",
                            "Please check your email to reset your password",
                            MotionToastStyle.INFO,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );
                    finish();
                } else {
                    progressDialog.dismiss();
                    MotionToast.Companion.darkToast(
                            this,
                            "Error",
                            "This account doesn't exist",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    );
                }
            });
        }
    }
}