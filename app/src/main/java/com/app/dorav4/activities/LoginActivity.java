package com.app.dorav4.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dorav4.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout tilEmailAddress, tilPassword;
    TextInputEditText etEmailAddress, etPassword;
    TextView tvForgotPassword;
    Intent intent;
    Button btnLogin, btnRegister;

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        etEmailAddress = findViewById(R.id.etEmailAddress);
        etPassword = findViewById(R.id.etPassword);
        tilEmailAddress = findViewById(R.id.tilEmailAddress);
        tilPassword = findViewById(R.id.tilPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        progressDialog = new ProgressDialog(LoginActivity.this);

        mAuth = FirebaseAuth.getInstance();

        // btnLogin OnClickListener
        btnLogin.setOnClickListener(v -> loginUser());

        // btnRegister OnClickListener
        btnRegister.setOnClickListener(v -> {
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // tvForgotPassword OnClickListener
        tvForgotPassword.setOnClickListener(v -> {
            intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    // Login User
    public void loginUser() {
        boolean isEmailValid = false, isPasswordValid = false;
        String email = Objects.requireNonNull(etEmailAddress.getText()).toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString().trim();

        // Validate email address
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmailAddress.setError("Invalid email address, please try again.");
        } else {
            isEmailValid = true;
            tilEmailAddress.setError(null);
        }

        // Validate password
        if (password.isEmpty()) {
            tilPassword.setError("Password cannot be empty!");
        } else {
            isPasswordValid = true;
            tilPassword.setError(null);
        }

        // If all inputs are valid
        if (isEmailValid && isPasswordValid) {
            // ProgressDialog
            progressDialog.setTitle("Login");
            progressDialog.setMessage("Verifying your login credentials");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Sign-in using Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;

                    // Email verification for first time logins.
                    if (user.isEmailVerified()) {
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        user.sendEmailVerification();

                        Toast.makeText(LoginActivity.this, "Please check your email address to verify your account", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
                clearFields();
            });
        }
    }

    // Clear login form
    private void clearFields() {
        Objects.requireNonNull(etEmailAddress.getText()).clear();
        Objects.requireNonNull(etPassword.getText()).clear();
        etEmailAddress.clearFocus();
        etPassword.clearFocus();
    }
}