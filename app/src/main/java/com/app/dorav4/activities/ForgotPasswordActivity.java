package com.app.dorav4.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.dorav4.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

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
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your email to reset your password", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this, "Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}